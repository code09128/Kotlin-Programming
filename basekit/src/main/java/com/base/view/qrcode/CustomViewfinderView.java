package com.base.view.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2018/1/8.
 */

@SuppressLint("DrawAllocation")
public class CustomViewfinderView extends ViewfinderView {

    /**重繪掃描線的時間間隔*/
    public static final long CUSTOME_ANIMATION_DELAY = 10;

    /* ******************************************    邊角線相關屬性    ************************************************/

    /**"邊角線長度/掃描邊框長度"的佔比(比例越大，線越長)*/
    public float mLineRate = 0.1F;

    /**邊角線厚度(建議使用dp)*/
    public float mLineDepth =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());

    /**邊角線顏色*/
    public int mLineColor = Color.RED;

    /* *******************************************    掃描線相關屬性    ************************************************/

    /**掃描線位置*/
    public int mScanLinePosition = 0;

    /**掃描線是否碰觸到頂端(移動折返跑時用)*/
    public boolean isTouchTop = true;

    /**掃描線厚度*/
    public float mScanLineDepth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

    /**掃描線每次重繪的移動距離*/
    public float mScanLineDy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

    /**線性梯度*/
    public LinearGradient mLinearGradient;

    /**線性梯度位置*/
    public float[] mPositions = new float[]{0f, 0.5f, 1f};

    /**線性梯度各個位置對應的顏色值*/
    public int[] mScanLineColor = new int[]{0x00FFFFFF, Color.CYAN, 0x00FFFFFF};

    public CustomViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        Rect frame = framingRect;
        Rect previewFrame = previewFramingRect;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        /*掃描框以外區域給予背景色*/
//        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        /*繪製4個角*/
        paint.setColor(mLineColor);//定義四角的顏色

        /*以下四角繪製皆為上橫下豎，形成一個直角*/
        /*左上*/
        canvas.drawRect(frame.left - mLineDepth, frame.top - mLineDepth, frame.left + frame.width() * mLineRate, frame.top , paint);
        canvas.drawRect(frame.left - mLineDepth, frame.top, frame.left, frame.top + frame.height() * mLineRate, paint);

        /*右上*/
        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.top - mLineDepth, frame.right + mLineDepth, frame.top, paint);
        canvas.drawRect(frame.right, frame.top, frame.right + mLineDepth, frame.top + frame.height() * mLineRate, paint);

        /*左下*/
        canvas.drawRect(frame.left - mLineDepth, frame.bottom, frame.left + frame.width() * mLineRate, frame.bottom + mLineDepth, paint);
        canvas.drawRect(frame.left - mLineDepth, frame.bottom - frame.height() * mLineRate, frame.left, frame.bottom, paint);

        /*右下*/
        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.bottom, frame.right + mLineDepth, frame.bottom + mLineDepth, paint);
        canvas.drawRect(frame.right, frame.bottom - frame.height() * mLineRate, frame.right + mLineDepth, frame.bottom, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        }
        /*繪製掃描線*/
        else{
            /*變換掃描線座標，碰到Top/Bottom就讓線回去(折返跑)*/
            if(isTouchTop){
                mScanLinePosition += mScanLineDy;

                if(mScanLinePosition > frame.height()){
                    mScanLinePosition -= mScanLineDy;
                    isTouchTop = false;
                }
            }else{
                mScanLinePosition -= mScanLineDy;

                if(mScanLinePosition < 0){
                    mScanLinePosition += mScanLineDy;
                    isTouchTop = true;
                }
            }

            /*位置確立後，繪出掃描線*/
            mLinearGradient = new LinearGradient(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition, mScanLineColor, mPositions, Shader.TileMode.CLAMP);
            paint.setShader(mLinearGradient);
            canvas.drawRect(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition + mScanLineDepth, paint);
            paint.setShader(null);

            /**/
            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;

            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint);
                }
            }

            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint);
                }
            }
        }

        /*
         * 建立重繪機制
         * 每次onDraw跑完後，間隔一段時間後重新執行
         * Note:傳入邊框參數，排除掃描框以外的區域重繪(只針對掃描線重新繪圖)*/
        postInvalidateDelayed(CUSTOME_ANIMATION_DELAY,
                frame.left,
                frame.top,
                frame.right,
                frame.bottom);
    }
}
