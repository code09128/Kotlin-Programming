package com.base.view.qrcode;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.base.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by Eric on 2018/1/8.
 */

public class CustomCaptureActivity extends AppCompatActivity {
    /**條形碼掃描管理器*/
    private CaptureManager mCaptureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        /*條形碼掃描視圖*/
        DecoratedBarcodeView mBarcodeView = findViewById(R.id.zxing_barcode_scanner);
        mCaptureManager = new CaptureManager(this, mBarcodeView);//初始化配置掃碼界面
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);//intent中攜帶了通過IntentIntegrator設置的參數
        mCaptureManager.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*使用者拒絕拍照/錄製影片權限，退出掃描頁面*/
        if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            this.finish();
        }
    }
}