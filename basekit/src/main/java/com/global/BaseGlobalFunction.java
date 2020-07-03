package com.global;

import android.app.Activity;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.base.utilities.JudgeIllegalUtil;
import com.base.view.qrcode.CustomCaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.global.BaseGlobalConfig.ADDITION;
import static com.global.BaseGlobalConfig.DIVISION;
import static com.global.BaseGlobalConfig.MULTIPLICATION;
import static com.global.BaseGlobalConfig.SUBTRACTION;


/*
 * Created by Eric on 2018/1/9.
 */

@SuppressWarnings("ALL")
public class BaseGlobalFunction {
    private static final String TAG = BaseGlobalFunction.class.getSimpleName();

    /**顯示Exception訊息在console*/
    public static void showErrorMessage(String tag,Exception e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        Log.e(tag, errors.toString());
    }

    /**訊息通知*/
    public static void setToast(Context context,String message){
        Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    /**掃描QRCode(Activity頁面叫用掃描頁)*/
    public static void scanQRCode(Activity activity){
        IntentIntegrator scanner = new IntentIntegrator(activity);
        scanQRCode(scanner);
    }

    /**掃描QRCode(Fragment頁面叫用掃描頁)*/
    public static void scanQRCode(Fragment fragment){
        IntentIntegrator scanner = IntentIntegrator.forFragment(fragment);
        scanQRCode(scanner);
    }

    /**啟動掃描頁*/
    private static void scanQRCode(IntentIntegrator scanner){
        scanner.setCaptureActivity(CustomCaptureActivity.class);//使用客製化掃描視窗
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanner.setBeepEnabled(false);//關閉掃描成功音效
        scanner.setOrientationLocked(true);//鎖住頁面旋轉
        scanner.setTimeout(BaseGlobalData.TIMEOUT);
        scanner.initiateScan();
    }
    
    /**Activity叫用Fragment*/
    public static void replaceFragment(Activity activity, int layout, final Fragment fragment) {
        FragmentTransaction fm = activity.getFragmentManager().beginTransaction();

        fm.addToBackStack(null)//按下返回鍵會回到上一個Fragment
                .replace(layout, fragment)
                .commit();
    }

    /**在Fragment加上Fragment(按返回鍵會destroy當前頁面)*/
    public static void addFragment(Activity activity, int layout, Fragment oldFragment, Fragment newFragment) {
        FragmentTransaction fm = activity.getFragmentManager().beginTransaction();

        fm.add(layout,newFragment,newFragment.getClass().getName())
                .addToBackStack(null)//按下返回鍵會回到上一個Fragment
                .show(newFragment);

        if(oldFragment != null){
            fm.hide(oldFragment);
        }

        fm.commit();
    }

    /**
     * 在Fragment跟Fragment之間的切換
     * @param activity 當前頁面的Activity
     * @param layout Fragment容器
     * @param originalFragment 當前頁面所顯示的Fragment
     * @param targetFragment 要準備顯示的Fragment
     * @param tagName fragment tag
     * @return 現在頁面所顯示的Fragment
     * */
    public static Fragment changeFragment(Activity activity,int layout,Fragment originalFragment,Fragment targetFragment,String tagName){
        FragmentTransaction fm = activity.getFragmentManager().beginTransaction();

        /*要顯示的頁面沒有新增過*/
        if (!targetFragment.isAdded()){
            /*隱藏當前Fragment頁面*/
            if(originalFragment != null){
                fm.hide(originalFragment);
            }

            fm.add(layout,targetFragment,tagName);//新增要顯示的頁面
        }
        /*隱藏當前Fragment頁面，顯示新的頁面*/
        else{
            fm.hide(originalFragment);
        }

        fm.show(targetFragment);
        fm.commit();

        return targetFragment;
    }

    /**取得當前Acticity*/
    public static Activity getActivity(){
        return BaseGlobalData.ACTIVITY.get();
    }

    /**檢查字串中是否含有特殊字元，避免SQL injection*/
    public static String preventString(String str) {
        return JudgeIllegalUtil.getJudgeIllegalUtil().getCorrect(str);
    }

    /**設定時間為00:00:00，24小時制*/
    public static Calendar setInitTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**載入網址*/
    public static void webViewProcess(WebView w_webPage, String webUrl){
        WebSettings wetSettings = w_webPage.getSettings();

        wetSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wetSettings.setJavaScriptEnabled(true);
        wetSettings.setAppCacheEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wetSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        w_webPage.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        w_webPage.loadUrl(webUrl);
    }

    /**執行震動*/
    public static void setVibrate(int time, Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    /**
     * 手機OS版本檢查
     * @return true:Android 8以上，false:Android 8以下
     * */
    public static boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 設定時間格式
     * @param format 要設定的時間格式
     */
    public static String adjustTimeFormat(String data,SimpleDateFormat sdf) {
        Date date = null;

        try {
            date = sdf.parse(data);
        } catch (ParseException e) {
            BaseGlobalFunction.showErrorMessage(TAG,e);
        }

        if(date == null){
            return "-";
        }else{
            return sdf.format(date);
        }
    }

    /**
     * 浮點數計算
     * @param values 運算參數
     * @param operation 運算模式ADDITION:加法 SUBTRACTION:減法 MULTIPLICATION:乘法 DIVISION:除法
     * */
    public static double decimalOperation(double[] values,int operation){
        BigDecimal result = new BigDecimal(String.valueOf(values[0]));
        boolean init = true;

        for(double bean:values){
            /*第一筆資料已放進result，從第二筆資料開始計算*/
            if(init){
                init = false;
                continue;
            }

            BigDecimal value = new BigDecimal(String.valueOf(bean));

            switch(operation){
                /*加法*/
                case ADDITION:
                    result = result.add(value);
                    break;
                /*減法*/
                case SUBTRACTION:
                    result = result.subtract(value);
                    break;
                /*乘法*/
                case MULTIPLICATION:
                    result = result.multiply(value);
                    break;
                /*除法*/
                case DIVISION:
                    /*如果有除以0的情形，則不進行運算*/
                    if(value.intValue() != 0){
                        /*除不盡，取到小數第六位*/
                        result = result.divide(value,6, BigDecimal.ROUND_HALF_EVEN);
                    }
                    break;
            }
        }

        return Double.valueOf(result.stripTrailingZeros().toPlainString());//回傳時去除小數點末尾多餘的0
    }

    /**上下極值擴展*/
    public static float[] setLimit(float min,float max){
        double distance = decimalOperation(new double[]{max,min},SUBTRACTION);//最大/最小值的間距
        double portion = decimalOperation(new double[]{distance,5},DIVISION);//距離切分5等分

        max = (float)decimalOperation(new double[]{max,portion},ADDITION);

        /*如果min等於0，就讓下限值等於0，不等於0才進行擴展*/
        if(min != 0){
            min = (float)decimalOperation(new double[]{min,portion},SUBTRACTION);
        }

        /*如果上下極值相同，則各展延10%*/
        if(max == min){
            /*如果上下極值皆為0*/
            if(max == 0){
                max = 1;
                min = 0;
            }else{
                max = max*1.1F;
                min = min*0.9F;
            }
        }

        return new float[]{min,max};
    }

    /**關閉編輯頁面的虛擬鍵盤*/
    public static void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            View view = getActivity().getCurrentFocus();

            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 將登入webservice回傳的字串變成Json格式的字串
     * @param xmlStr XML的字串
     * @return Json格式的字串
     */
    public static String convertXMLString(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));//將webservice取來的字串變成DOC格式

            Element rootElement = doc.getDocumentElement();//取到<string>下的元素

            String result = rootElement.getTextContent();//取出<string>下面的所有內容

            Document document = builder.parse(new InputSource(new StringReader(result)));//再次把字串轉成DOC格式

            Element rootNode = document.getDocumentElement();//取到<root>下的元素

            NodeList CLNodeList = rootNode.getChildNodes();//取到<root>下面的所有子節點,就是<CL>
            NodeList subNodeList = CLNodeList.item(0).getChildNodes();//取到<CL>下面的所有子節點,就是<R>,<K>
            String token = subNodeList.item(1).getTextContent();//<R>裡面的內容,就是token

            return token;
        } catch (Exception e) {
            showErrorMessage(TAG,e);
        }

        return null;
    }
}