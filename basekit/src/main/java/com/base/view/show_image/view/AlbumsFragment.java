package com.base.view.show_image.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.base.R;
import com.base.R2;
import com.base.view.show_image.adapter.AlbumsAdapter;
import com.base.view.show_image.utilities.RecyclerViewOnScrollListener;
import com.global.ActionCallback;
import com.global.BaseGlobalConfig;
import com.global.BaseGlobalFunction;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/*
 * Created by Eric on 2018/6/15
 */
@SuppressLint("StaticFieldLeak")
public class AlbumsFragment extends Fragment implements RecyclerViewOnScrollListener.OnScrollListener,ActionCallback.data<Bitmap>{
    private static AlbumsFragment fragment;

    private final String TAG = getClass().getSimpleName();
    private final int ALBUMS_CODE = 0x01;//啟動相本功能

    private int layout;//套疊框架
    private ActionCallback.data<Bitmap> callback;
    private Unbinder unbinder;
    private ActionBar actionBar;
    private AlbumsAdapter adapter;//顯示縮圖

    @BindView(R2.id.r_recyclerView)
    RecyclerView r_recyclerView;

    public static AlbumsFragment getInstance() {
        if (fragment == null) {
            fragment = new AlbumsFragment();
        }

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);

        setHasOptionsMenu(true);//調用Fragment的onCreateOptionsMenu()方法
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actionBar.setTitle(getString(R.string.my_albums));

        GridLayoutManager grid = new GridLayoutManager(getContext(), BaseGlobalConfig.RAW_ITEM_COUNT);
        r_recyclerView.setLayoutManager(grid);

        r_recyclerView.smoothScrollToPosition(0);
        r_recyclerView.setBackgroundColor(Color.BLACK);
        r_recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(this));
        ((DefaultItemAnimator) r_recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        init();
    }

    /**基本參數設定*/
    public void setData(int layout,ActionCallback.data<Bitmap> callback){
        this.layout = layout;
        this.callback = callback;
    }

    /**取出全部圖片並顯示*/
    private void init(){
        ContentResolver cr = getActivity().getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA };

        /*查詢SD卡的圖片*/
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, null, null, null);

        if(cursor != null){
            ArrayList<String> picId = new ArrayList<>();//存放縮圖的id
            ArrayList<String> imagePaths = new ArrayList<>();//存放圖片的路徑

            for(int i=0; i<cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));//ID
                picId.add(String.valueOf(id));

                String filepath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//抓路徑

                imagePaths.add(filepath);
            }

            cursor.close();

            adapter = new AlbumsAdapter(getContext(),layout,picId,imagePaths,this);
            adapter.showPicture(0,BaseGlobalConfig.SHOW_ITEM_COUNT-1);//繪製15格顯示畫面
            r_recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱*/
        menu.add(0, 0, 0, getString(R.string.native_album));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /*返回*/
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
            /*原生相本*/
            case 0:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");//只取圖片
                startActivityForResult(intent, ALBUMS_CODE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch(requestCode){
                /*從原生相簿抓取到照片，進行預覽*/
                case ALBUMS_CODE:
                    try{
                        Uri uri = data.getData();

                        if (uri != null) {
                            Uri imageUri = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                            PreviewPhotoFragment fragment = PreviewPhotoFragment.getInstance();
                            fragment.setInit(bitmap,true,this);

                            BaseGlobalFunction.addFragment(getActivity(),layout,AlbumsFragment.getInstance(),fragment);
                        }
                    }catch(Exception e){
                        BaseGlobalFunction.showErrorMessage(TAG,e);
                    }

                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onScroll(int firstIndex, int lastIndex){
        adapter.showPicture(firstIndex,lastIndex);
    }

    @Override
    public void onDataResult(Bitmap result){
        /*預覽點選確認按鈕*/
        if(result != null){
            callback.onDataResult(result);
            getFragmentManager().popBackStack();
        }
    }
}
