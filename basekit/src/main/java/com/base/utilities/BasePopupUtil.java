package com.base.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.base.R;
import com.global.ActionCallback;
import com.global.BaseGlobalData;

import java.util.Objects;

/**
 * Created by Eric on 2017/12/25.
 */

@SuppressLint("InflateParams")
public class BasePopupUtil {
    private static BasePopupUtil basePopupUtil;

    private ProgressDialog loadingDialog;
    private ProgressDialog progressBar;

    /**提示視窗模組*/
    public static BasePopupUtil getBasePopupUtil(){
        if(basePopupUtil == null){
            basePopupUtil = new BasePopupUtil();
        }

        return basePopupUtil;
    }

    /**Loading動畫視窗*/
    public void showLoadingPopup(Context context, String message) {
        /*多執行緒同時發出請求時，如果不存在前一個Popup，才可建立新的*/

        if (!((Activity) context).isFinishing()) {
            //show dialog
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(context, R.style.customDialog);

                loadingDialog.setCanceledOnTouchOutside(false);//能否透過點擊外圍關閉
                loadingDialog.setCancelable(false);//能否按返回鍵關閉
                loadingDialog.show();

                loadingDialog.setContentView(R.layout.loading_animation);
                Objects.requireNonNull(loadingDialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView t_message = loadingDialog.findViewById(R.id.t_message);
                t_message.setText(message);
            }
        }
    }

    /**進度條*/
    public void showProgressPopup(Context context, int percent, View.OnClickListener listener){
        if(progressBar == null){
            progressBar = new ProgressDialog(context,R.style.customDialog);

            progressBar.setCanceledOnTouchOutside(false);//能否透過點擊外圍關閉
            progressBar.setCancelable(false);//能否按返回鍵關閉
            progressBar.show();

            progressBar.setContentView(R.layout.popup_progress_bar);
            Objects.requireNonNull(progressBar.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ProgressBar p_progress = progressBar.findViewById(R.id.p_progress);
        Button b_cancel = progressBar.findViewById(R.id.b_cancel);
        TextView t_value = progressBar.findViewById(R.id.t_percent);

        p_progress.setProgress(percent);
        t_value.setText(String.valueOf(percent));

        b_cancel.setOnClickListener(listener);
    }

    /**關閉進度條視窗*/
    public void closeProgressPopup(){
        if(progressBar != null){
            progressBar.dismiss();
            progressBar = null;
        }
    }

    /**關閉Loading視窗*/
    public void closeLoadingPopup(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**純通知視窗(確認/取消)*/
    public void showAlertDialog(Context context, String message, boolean haveNo, final ActionCallback.bool callback){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);

        alertbox.setTitle(BaseGlobalData.APP_NAME);
        alertbox.setIcon(null);
        alertbox.setMessage(message);
        alertbox.setCancelable(false);//點選空白處不關閉Popup
        alertbox.setPositiveButton(context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callback != null){
                            callback.onBooleanResult(true);
                        }
                    }
                });

        /*是否要顯示取消按紐*/
        if(haveNo){
            alertbox.setNeutralButton(context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(callback != null){
                                callback.onBooleanResult(false);
                            }
                        }
                    });
        }

        setButtonStyle(context,alertbox);
    }

    /**建立Button樣式*/
    private void setButtonStyle(Context context,AlertDialog.Builder alertbox){
        AlertDialog alert = alertbox.create();
        alert.show();

        Button b_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        b_yes.setTextColor(context.getColor(R.color.steelblue));

        Button b_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        b_no.setTextColor(context.getColor(R.color.indianred));
    }
}
