package com.base.view.show_image.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.base.R;
import com.base.R2;
import com.base.view.show_image.utilities.ImageViewHelper;
import com.global.ActionCallback;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/*
 * Created by Eric on 2018/6/13
 */
@SuppressLint("StaticFieldLeak")
public class PreviewPhotoFragment extends Fragment {
    private static PreviewPhotoFragment fragment;

    @BindView(R2.id.i_picture)
    ImageView i_picture;
    @BindView(R2.id.b_cancel)
    Button b_cancel;
    Unbinder unbinder;

    private Bitmap bitmap;
    private boolean haveCancel;
    private ActionCallback.data<Bitmap> callback;

    public static PreviewPhotoFragment getInstance(){
        if(fragment == null){
            fragment = new PreviewPhotoFragment();
        }

        return fragment;
    }

    /**
     * 初始設定
     * @param bitmap image圖像
     * @param haveCancel 是否需要取消鍵
     */
    public void setInit(Bitmap bitmap, boolean haveCancel,ActionCallback.data<Bitmap> callback){
        this.bitmap = bitmap;
        this.haveCancel = haveCancel;
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_photo, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(haveCancel){
            b_cancel.setVisibility(View.VISIBLE);
        }else{
            b_cancel.setVisibility(View.GONE);
        }

        if(i_picture != null && bitmap != null){
            /*圖片縮放，及調整最適寬高*/
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            new ImageViewHelper(dm, i_picture, bitmap);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.b_ok, R2.id.b_cancel})
    public void onViewClicked(View view) {
        int id = view.getId();

        if(id == R.id.b_ok){
            if(callback != null){
                callback.onDataResult(bitmap);
            }

            getFragmentManager().popBackStack();
        }else if(id == R.id.b_cancel){
            if(callback != null){
                callback.onDataResult(null);
            }

            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                /*按back鍵回上一頁*/
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    getFragmentManager().popBackStack();
                    return true;
                }

                return false;
            }
        });
    }
}
