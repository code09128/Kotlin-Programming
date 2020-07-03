package com.base.view.show_image.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;

import com.base.R;
import com.base.R2;
import com.base.adapter.RecyclerAdapter;
import com.base.view.show_image.view.AlbumsFragment;
import com.base.view.show_image.view.PreviewPhotoFragment;
import com.global.ActionCallback;
import com.global.BaseGlobalConfig;
import com.global.BaseGlobalFunction;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Eric on 2018/6/15
 */
public class AlbumsAdapter extends RecyclerAdapter<RecyclerAdapter.Holder> implements ActionCallback.data<Bitmap>{
    private final String TAG = getClass().getSimpleName();

    private LruCache<Integer,Bitmap> bmDatas;//記錄已繪好的縮圖
    private ArrayList<String> filespath;//存放圖片的路徑
    private ArrayList<String> picId;//存放縮圖的id
    private Context context;
    private int layout;
    private ActionCallback.data<Bitmap> callback;

    private int oldFirstIndex = 0;//手機畫面目前顯示的最上面list的索引值
    private int oldLastIndex = 0;//手機畫面目前顯示的最後一個list的索引值

    public AlbumsAdapter(Context context,int layout,ArrayList<String> picId, ArrayList<String> filespath,ActionCallback.data<Bitmap> callback){
        this.context = context;
        this.layout = layout;
        this.picId = picId;
        this.filespath = filespath;
        this.callback = callback;

        //獲取到App的最大內存
        int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        //設置LruCache的緩存大小
        int cacheSize = maxMemory/8;

        bmDatas = new LruCache<Integer,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value){
                return (value.getRowBytes()*value.getHeight())/1024;
            }
        };
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_photo_item, parent, false);

        return new GridHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position){
        super.onBindViewHolder(holder, position);
        GridHolder list = (GridHolder) holder;

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;//取出螢幕寬度
        int newWidth = (int)(screenWidth/BaseGlobalConfig.RAW_ITEM_COUNT);//一列顯示三個縮圖

        /*初次設定:把圖格設為正方形*/
        if(list.c_itemPhoto.getLayoutParams().height != newWidth){
            list.c_itemPhoto.setLayoutParams(new GridView.LayoutParams(newWidth, newWidth));
        }

        /*如果position位置在顯示頁面上6格、15格顯示畫面、顯示頁面下6格，繪製圖片*/
        if(oldFirstIndex - (BaseGlobalConfig.RAW_ITEM_COUNT*2) <= position &&
           position <= oldLastIndex + (BaseGlobalConfig.RAW_ITEM_COUNT*2)){
            setImage(list,position);
        }
        /*隱藏圖片*/
        else{
            list.f_mask.setVisibility(View.VISIBLE);
        }
    }

    /**顯示圖片*/
    public void showPicture(int NewFirstIndex, int NewLastIndex){
        /* 向上/向下滑動超過兩列(6格)*/
        if(oldFirstIndex - (BaseGlobalConfig.RAW_ITEM_COUNT*2) > NewFirstIndex ||
           oldLastIndex + (BaseGlobalConfig.RAW_ITEM_COUNT*2) < NewLastIndex){
            oldFirstIndex = NewFirstIndex;
            oldLastIndex = NewLastIndex;

            notifyItemRangeChanged(0,getItemCount());
        }
    }

    /**繪製小縮圖*/
    private void setImage(final GridHolder holder,final int position){
        new Thread(new Runnable(){
            public void run(){
                synchronized(handler){
                    try {
                        if(bmDatas.get(position) == null){
                            Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(
                                    context.getApplicationContext().getContentResolver(),
                                    Long.parseLong(picId.get(position)),
                                    MediaStore.Images.Thumbnails.MINI_KIND, null);

                            bmDatas.put(position, bm);
                        }

                        Message message = handler.obtainMessage(0, holder);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        message.setData(bundle);

                        handler.sendMessage(message);
                    }catch(Exception e){
                        BaseGlobalFunction.showErrorMessage(TAG,e);
                    }
                }
            }
        }).start();
    }

    /**顯示grid單格圖片*/
    private final Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            GridHolder holder = (GridHolder)msg.obj;
            ImageButton imageButton = holder.i_photo;
            FrameLayout mask = holder.f_mask;

            int position = msg.getData().getInt("position");
            Bitmap bm = bmDatas.get(position);

            setImageButton(mask,imageButton, bm, position);
        }
    };

    /**把縮圖放進imageButton裡*/
    private void setImageButton(FrameLayout mask,ImageButton imageButton, Bitmap bm, final int position){
        if(bm != null){
            mask.setVisibility(View.GONE);
            imageButton.setImageBitmap(bm);

            /*點擊照片進行預覽*/
            imageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try{
                        Bitmap bitmap = BitmapFactory.decodeFile(filespath.get(position));
                        PreviewPhotoFragment fragment = PreviewPhotoFragment.getInstance();

                        fragment.setInit(bitmap,true,AlbumsAdapter.this);
                        BaseGlobalFunction.addFragment((Activity)context,layout,AlbumsFragment.getInstance(),fragment);
                    }catch(Exception e){
                        BaseGlobalFunction.showErrorMessage(TAG,e);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return picId.size();
    }

    @Override
    public void onDataResult(Bitmap result){
        callback.onDataResult(result);
    }

    class GridHolder extends Holder{
        @BindView(R2.id.c_itemPhoto)
        ConstraintLayout c_itemPhoto;
        @BindView(R2.id.i_photo)
        ImageButton i_photo;
        @BindView(R2.id.f_mask)
        FrameLayout f_mask;//防止刷新圖像時閃屏

        GridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
