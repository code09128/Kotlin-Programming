package com.base.view.show_image.utilities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/*
 * Created by Eric on 2018/6/12
 */
/**圖片縮放*/
@SuppressLint("ClickableViewAccessibility")
public class ImageViewHelper {
    private Matrix matrix = new Matrix();//初始狀態的Matrix
    private Matrix changeMatrix = new Matrix();//進行變動狀況下的Matrix
    private ImageView imageView;
    private Bitmap bitmap;

    private final int NONE = 0;// 初始狀態
    private final int DRAG = 1;// 拖曳狀態
    private final int ZOOM = 2;// 縮放狀態
    private int mode = NONE;//目前模式

    private PointF firstPointF = new PointF();//第一點按下的座標
    private PointF secondPointF = new PointF();//第二點按下的座標
    private float distance = 1f;//兩點距離
    private DisplayMetrics dm;

    private double heightProportion = 0.85843230404;//(按鈕高度/螢幕高度)，因為有加確認按鈕，所以不能用手機原始高度
    private float minScaleR;//最小縮放比例
	private float maxScaleR = 15f;//最大縮放比例

	/**
     * 建立縮放
     * @param dm 螢幕尺寸
     * @param imageView 元件
     * @param bitmap image圖像
     * */
    public ImageViewHelper(DisplayMetrics dm,ImageView imageView,Bitmap bitmap){
        this.dm = dm;
        this.imageView = imageView;
        this.bitmap = bitmap;

        setImageSize();
        minZoom();
        center();

        imageView.setImageMatrix(matrix);
        imageView.setImageBitmap(bitmap);
    }

    /**把圖片縮放到合適大小*/
    private void minZoom() {
        /* 取得最小的比例，假設圖片比螢幕大
         * 則寬高其中一項比例必小於1，需將圖片進行縮小
         * 若寬高皆比螢幕小，則比例必大於1，取最小比例(最接近螢幕的數值)進行放大，而圖片越小，放大倍數會越大
         * 如果螢幕跟圖片大小相同，則倍數會為1
         *
         * ex:
         * dm.widthPixels   dm.heightPixels   bitmap.getWidth()   bitmap.getHeight()	   縮放比例
         * 		1280			 2105				352					240				  3.6363637
         * 		1280			 2105				5344				3008			  0.23952095
         * 以上面資料來說，比例上圖片寬度皆最接近螢幕寬度，所以取寬度比例，把寬高按照該比例放大，讓圖片寬=螢幕寬，圖片高也等比例縮放，讓圖片不變形
         * 不過因為高度有涵蓋按鈕，需扣掉按鈕高度
         */
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) (dm.heightPixels* heightProportion) / (float) bitmap.getHeight());

        matrix.postScale(minScaleR, minScaleR);
    }

    /**橫向、縱向置中*/
    private void center() {
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        matrix.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        /*
         * 圖片滑動後，手勢放開時恢復原來置中位置
         * 圖片橫向滑動處理
         * */
        int screenWidth = dm.widthPixels;

        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        }
        /*左滑*/
        else if (rect.left > 0) {
            deltaX = -rect.left;
        }
        /*右滑*/
        else if (rect.right < screenWidth) {
            deltaX = screenWidth - rect.right;
        }

        /*
         * 圖片直向滑動處理
         * 圖片小於螢幕大小，則置中顯示。
         * 大於螢幕，上方則留空白則往上移，下方留空白則往下移
         */
        int screenHeight = (int)(dm.heightPixels * heightProportion);

        /*圖片比螢幕小*/
        if(height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top;
        }
        /*下滑*/
        else if (rect.top > 0) {
            deltaY = -rect.top;
        }
        /*上滑*/
        else if (rect.bottom < screenHeight) {
            deltaY = imageView.getHeight() - rect.bottom;
        }

        matrix.postTranslate(deltaX, deltaY);
    }

    /**兩點的距離*/
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x*x + y*y);
    }

    /**兩點的中點*/
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);

        point.set(x/2, y/2);
    }

    /**控制縮放比例*/
    private void controlScale(){
        float values[] = new float[9];
        matrix.getValues(values);

        if(mode == ZOOM){
            /*避免無限縮小*/
            if(values[0] < minScaleR){
                matrix.setScale(minScaleR, minScaleR);
            }
            /*避免無限放大*/
  			else if(values[0] > maxScaleR){
  				matrix.set(changeMatrix);
  			}
        }
    }

    /**建立圖片移動縮放事件 */
    private void setImageSize(){
        imageView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    /*第一點按下進入*/
                    case MotionEvent.ACTION_DOWN:
                        changeMatrix.set(matrix);
                        firstPointF.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    /*第二點按下進入*/
                    case MotionEvent.ACTION_POINTER_DOWN:
                        distance = spacing(event);

                        /*如果兩點距離超過10, 就判斷為多點觸控模式 即為縮放模式*/
                        if (spacing(event) > 10f){
                            changeMatrix.set(matrix);
                            midPoint(secondPointF, event);
                            mode = ZOOM;
                        }

                        break;
                    /*離開觸碰*/
                    case MotionEvent.ACTION_UP:
                    /*離開觸碰，狀態恢復*/
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    /*滑動過程*/
                    case MotionEvent.ACTION_MOVE:
                        if(mode == DRAG){
                            matrix.set(changeMatrix);
                            matrix.postTranslate(event.getX()- firstPointF.x, event.getY()- firstPointF.y);
                        }
                        else if(mode == ZOOM){
                            float newDist = spacing(event);//偵測兩根手指移動的距離

                            if (newDist > 10f){
                                matrix.set(changeMatrix);
                                float tScale = newDist / distance;
                                matrix.postScale(tScale, tScale, secondPointF.x, secondPointF.y);
                            }
                        }

                        break;
                }

                controlScale();
                center();

                imageView.setImageMatrix(matrix);

                return true;
            }
        });
    }
}
