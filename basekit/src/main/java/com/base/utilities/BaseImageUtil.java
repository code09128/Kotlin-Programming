package com.base.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;

import com.global.BaseGlobalFunction;

import java.io.IOException;
import java.io.InputStream;

/*
 * Created by Eric on 2018/6/12
 */
public class BaseImageUtil {
    private static BaseImageUtil baseImageUtil;
    private final String tag = getClass().getSimpleName();

    public static BaseImageUtil getBaseImageUtil(){
        if(baseImageUtil == null){
            baseImageUtil = new BaseImageUtil();
        }

        return baseImageUtil;
    }

    /**調整照片大小，size=最短寬px或高px，size=0:不調整*/
    public Bitmap reSize(Bitmap bitmap,int size){
        Bitmap marker = null;
        int Nwidth;//新寬度
        int Nheight;//新長度
        float rate = 1;

        if(bitmap != null){
            int bmpWidth = bitmap.getWidth();//取得原圖檔寬度
            int bmpHeight = bitmap.getHeight();//取得原圖檔高度

            if(size != 0){
                /*橫向照片*/
                if(bmpWidth > bmpHeight){
//                    if(bmpHeight > size){
                        rate = (float)size/bmpHeight;
//                    }
                }
                /*直向照片*/
                else{
//                    if(bmpWidth > size){
                        rate = (float)size/bmpWidth;
//                    }
                }
            }

            Nwidth = (int)(bmpWidth*rate);//新寬度
            Nheight = (int)(bmpHeight*rate);//新長度

            marker = Bitmap.createBitmap(Nwidth, Nheight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(marker);

            try{
                canvas.drawBitmap(bitmap, new Rect(0, 0, bmpWidth, bmpHeight), new Rect(0, 0, Nwidth, Nheight), null);
            }catch(Exception e){
                BaseGlobalFunction.showErrorMessage(tag,e);
            }
        }

        return marker;
    }

    /**
     * 調整原圖尺寸以及圖片方向
     * @param bitmap 原圖bitmap
     * @param uri    原圖uri
     * @param size   要得尺寸
     */
    public Bitmap modifyBitmap(Context context,Bitmap bitmap, Uri uri, int size) throws IOException {
        Bitmap finalpicture;
        Bitmap resizeBitmap = baseImageUtil.reSize(bitmap, size);//壓縮圖片避免OutOfMemory

        /*透過Uri取得原始圖片的inputStream,為了建立ExifInterface 解析圖片各種屬性*/
        InputStream inputStream = null;
        if (uri != null) {
            inputStream = context.getContentResolver().openInputStream(uri);
        }

        ExifInterface exif;
        Matrix matrix = new Matrix();
        /*旋轉照片到正確的方向*/

        assert inputStream != null;
        exif = new ExifInterface(inputStream);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);//回傳照片屬性值

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                matrix.postRotate(0);
                break;

        }
        /*產生新的正確bitmap檔案*/
        finalpicture = Bitmap.createBitmap(resizeBitmap, 0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight(), matrix, true);

        return finalpicture;
    }
}
