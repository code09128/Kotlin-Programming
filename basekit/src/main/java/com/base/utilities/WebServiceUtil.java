package com.base.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.global.ActionCallback;
import com.global.BaseGlobalConfig;
import com.global.BaseGlobalData;
import com.global.BaseGlobalFunction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.global.BaseGlobalConfig.WEB_SERVICE_RESULT;

/**
 * Created by Eric on 2017/12/21.
 */

public class WebServiceUtil {
    private String tag = getClass().getSimpleName();
    private static WebServiceUtil webServiceUtil;
    private CallbackListeners callback;
    private BasePopupUtil basePopupUtil = BasePopupUtil.getBasePopupUtil();

    public interface CallbackListeners {
        void onTimeoutResult();
        void onHttpsError401Result();
        void onHttpsError500Result();
        void onHttpsOtherErrorResult();
    }

    /**發生Https錯誤時的回調通知*/
    public void setCallbackListeners(CallbackListeners callback) {
        this.callback = callback;
    }

    /**WebService連線模組*/
    public static WebServiceUtil getWebServiceUtil() {
        if (webServiceUtil == null) {
            webServiceUtil = new WebServiceUtil();
        }

        return webServiceUtil;
    }

    /**
     * POST模式，跟Server取資料
     * @param apiUrl Api位置
     * @param paras 請求參數(key/value)
     * @param token Token
     * @param message 加載動畫訊息
     * @param isCloseProgress 關閉加載動畫開關
     * @param callback Dao層callback回調
     * */
    public void getResponse_POST(final String apiUrl,
                                 final LinkedHashMap<String,Object> paras,
                                 final String token,
                                 final int message,
                                 final boolean isCloseProgress,
                                 final ActionCallback.dataResult callback){
        showLoadingPopup(message);

        /*走https連線*/
        if(apiUrl.contains("https://")){
            new Thread(new WebServiceHttpsRunnable(apiUrl,paras,null,token,isCloseProgress,callback)).start();
        }
        /*走http連線*/
        else{
            new Thread(new WebServiceHttpRunnable(apiUrl,paras,null,token,isCloseProgress,callback)).start();
        }
    }

    /**
     * POST模式，跟Server取資料
     * @param apiUrl Api位置
     * @param jsonO 請求參數(Json)
     * @param token Token
     * @param message 加載動畫訊息
     * @param isCloseProgress 關閉加載動畫開關
     * @param callback Dao層callback回調
     * */
    public void getResponse_POST(final String apiUrl,
                                 final JSONObject jsonO,
                                 final String token,
                                 final int message,
                                 final boolean isCloseProgress,
                                 final ActionCallback.dataResult callback){
        showLoadingPopup(message);

        /*走https連線*/
        if(apiUrl.contains("https://")){
            new Thread(new WebServiceHttpsRunnable(apiUrl,null,jsonO,token,isCloseProgress,callback)).start();
        }
        /*走http連線*/
        else{
            new Thread(new WebServiceHttpRunnable(apiUrl,null,jsonO,token,isCloseProgress,callback)).start();
        }
    }

    /**Http連線*/
    class WebServiceHttpRunnable implements Runnable{
        private String apiUrl;
        private LinkedHashMap<String,Object> paras;
        private JSONObject jsonO;
        private String token;
        private boolean isCloseProgress;
        private ActionCallback.dataResult callback;
        private StringBuilder sb = new StringBuilder();

        WebServiceHttpRunnable(final String apiUrl,
                               final LinkedHashMap<String,Object> paras,
                               final JSONObject jsonO,
                               final String token,
                               final boolean isCloseProgress,
                               final ActionCallback.dataResult callback){
            this.apiUrl = apiUrl;
            this.paras = paras;
            this.jsonO = jsonO;
            this.token = token;
            this.isCloseProgress = isCloseProgress;
            this.callback = callback;
        }

        @Override
        public void run(){
            HttpURLConnection conn = null;
            Bundle bundle = new Bundle();

            try{
                URL url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Charsert", "UTF-8");//設置請求編碼
                conn.setRequestProperty("Authorization", token);

                conn.setDoInput(true);//設為可下載
                conn.setDoOutput(true);//設為可上傳
                conn.setUseCaches(false);//不使用緩存
                conn.setConnectTimeout(BaseGlobalData.TIMEOUT);
                conn.setReadTimeout(BaseGlobalData.TIMEOUT);

                /*請求參數*/
                if(paras != null && paras.size() > 0){
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    sb.setLength(0);//清除內容

                    for (Map.Entry<String, Object> entry : paras.entrySet()) {
                        String value = String.valueOf(entry.getValue());
                        sb.append(entry.getKey()).append("=").append(value).append("&");
                    }

                    sb.setLength(sb.length()-1);//去除最後一個&字元

                    DataOutputStream ds = new DataOutputStream(conn.getOutputStream());

                    ds.write(sb.toString().getBytes("UTF-8"));
                    ds.flush();//close streams
                    ds.close();//關閉DataOutputStream
                }
                /*請求參數Json格式*/
                else{
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
                    String value = String.valueOf(jsonO);

                    ds.write(value.getBytes("UTF-8"));
                    ds.flush();//close streams
                    ds.close();//關閉DataOutputStream
                }

                sb.setLength(0);//清除內容

                /*Http 200*/
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();//開啟寫入串流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));//開啟讀取緩衝
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bundle.putInt("status", BaseGlobalConfig.SUCCESS);
                }
                /*Http 401*/
                else if(conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
                    bundle.putInt("status", BaseGlobalConfig.HTTPS_ERROR_401);
                }
                else if (conn.getResponseCode() == HttpsURLConnection.HTTP_INTERNAL_ERROR ){
                    bundle.putInt("status", BaseGlobalConfig.HTTPS_ERROR_500);
                }
                /*其他錯誤*/
                else{
                    bundle.putInt("status",BaseGlobalConfig.OTHER_ERROR);
                }
            }
            /*請求逾時*/
            catch(java.net.SocketTimeoutException e){
                sb.setLength(0);//清除內容
                BaseGlobalFunction.showErrorMessage(tag, e);
                bundle.putInt("status",BaseGlobalConfig.TIMEOUT);
            }
            /*其他錯誤*/
            catch(Exception e){
                sb.setLength(0);//清除內容
                BaseGlobalFunction.showErrorMessage(tag, e);
                bundle.putInt("status",BaseGlobalConfig.OTHER_ERROR);
            }
            finally{
                if (conn != null) {
                    conn.disconnect();
                }

                bundle.putString("result",sb.length() == 0? null:sb.toString());
                bundle.putBoolean("isCloseProgress", isCloseProgress);
                bundle.putSerializable("callback", callback);

                Message msg = new Message();
                msg.setData(bundle);

                handler.sendMessage(msg);
            }
        }
    }

    /**Https連線*/
    class WebServiceHttpsRunnable implements Runnable{
        private String apiUrl;
        private LinkedHashMap<String,Object> paras;
        private JSONObject jsonO;
        private String token;
        private boolean isCloseProgress;
        private ActionCallback.dataResult callback;
        private StringBuilder sb = new StringBuilder();

        WebServiceHttpsRunnable(final String apiUrl,
                               final LinkedHashMap<String,Object> paras,
                               final JSONObject jsonO,
                               final String token,
                               final boolean isCloseProgress,
                               final ActionCallback.dataResult callback){
            this.apiUrl = apiUrl;
            this.paras = paras;
            this.jsonO = jsonO;
            this.token = token;
            this.isCloseProgress = isCloseProgress;
            this.callback = callback;
        }

        @Override
        public void run(){
            HttpsURLConnection conn = null;
            Bundle bundle = new Bundle();

            try{
                URL url = new URL(apiUrl);
                conn = (HttpsURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Charsert", "UTF-8");//設置請求編碼
                conn.setRequestProperty("Authorization", token);

                conn.setDoInput(true);//設為可下載
                conn.setDoOutput(true);//設為可上傳
                conn.setUseCaches(false);//不使用緩存
                conn.setConnectTimeout(BaseGlobalData.TIMEOUT);
                conn.setReadTimeout(BaseGlobalData.TIMEOUT);

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{tm}, null);

                conn.setSSLSocketFactory(sc.getSocketFactory());
                conn.setHostnameVerifier(hnv);

                /*請求參數*/
                if(paras != null && paras.size() > 0){
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    sb.setLength(0);//清除內容

                    for (Map.Entry<String, Object> entry : paras.entrySet()) {
                        String value = String.valueOf(entry.getValue());
                        sb.append(entry.getKey()).append("=").append(value).append("&");
                    }

                    sb.setLength(sb.length()-1);//去除最後一個&字元

                    DataOutputStream ds = new DataOutputStream(conn.getOutputStream());

                    ds.write(sb.toString().getBytes("UTF-8"));
                    ds.flush();//close streams
                    ds.close();//關閉DataOutputStream
                }
                /*請求參數Json格式*/
                else{
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
                    String value = String.valueOf(jsonO);

                    ds.write(value.getBytes("UTF-8"));
                    ds.flush();//close streams
                    ds.close();//關閉DataOutputStream
                }

                sb.setLength(0);//清除內容

                /*Https 200*/
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();//開啟寫入串流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));//開啟讀取緩衝
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bundle.putInt("status", BaseGlobalConfig.SUCCESS);
                }
                /*Https 401*/
                else if(conn.getResponseCode() == HttpsURLConnection.HTTP_UNAUTHORIZED){
                    bundle.putInt("status", BaseGlobalConfig.HTTPS_ERROR_401);
                }
                /*Https 500*/
                else if(conn.getResponseCode() == HttpsURLConnection.HTTP_INTERNAL_ERROR){
                    bundle.putInt("status", BaseGlobalConfig.HTTPS_ERROR_500);
                }
                /*其他錯誤*/
                else{
                    bundle.putInt("status",BaseGlobalConfig.OTHER_ERROR);
                }
            }
            /*請求逾時*/
            catch(java.net.SocketTimeoutException e){
                sb.setLength(0);//清除內容
                BaseGlobalFunction.showErrorMessage(tag, e);
                bundle.putInt("status",BaseGlobalConfig.TIMEOUT);
            }
            /*其他錯誤*/
            catch(Exception e){
                sb.setLength(0);//清除內容
                BaseGlobalFunction.showErrorMessage(tag, e);
                bundle.putInt("status",BaseGlobalConfig.OTHER_ERROR);
            }
            finally{
                if (conn != null) {
                    conn.disconnect();
                }

                bundle.putString("result",sb.length() == 0? null:sb.toString());
                bundle.putBoolean("isCloseProgress", isCloseProgress);
                bundle.putSerializable("callback", callback);

                Message msg = new Message();
                msg.setData(bundle);

                handler.sendMessage(msg);
            }
        }
    }

    /**取得Response後，切回主執行緒，進行Callback回調*/
    private Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int status = msg.getData().getInt("status");
            String result = msg.getData().getString("result");
            boolean isCloseProgress = msg.getData().getBoolean("isCloseProgress");

            ActionCallback.dataResult callback = (ActionCallback.dataResult)msg.getData().getSerializable("callback");
            assert callback != null;

            try {
                JSONObject json = null;

                if (result != null) {
                    Object data = new JSONTokener(result).nextValue();
                    json = new JSONObject();

                    if (data instanceof JSONObject) {
                        json = (JSONObject) data;
                    }
                    /*如果是JSONArray 放入json*/
                    else if (data instanceof JSONArray) {
                        json.put(WEB_SERVICE_RESULT, data);
                    }
                    /*回傳值不是json格式的時候*/
                    else {
                        json.put(WEB_SERVICE_RESULT, result);
                    }
                }

                responseResult(status, json, isCloseProgress, callback);
            } catch (Exception e) {
                basePopupUtil.closeLoadingPopup();
                BaseGlobalFunction.showErrorMessage(tag, e);
                callback.onDataResult(null);
            }
        }
    };

    /**Response結果處理*/
    private void responseResult(int status, JSONObject result, boolean isCloseProgress, ActionCallback.dataResult daoCallback) {
        /*關閉動畫*/
        if (isCloseProgress || status != BaseGlobalConfig.SUCCESS) {
            basePopupUtil.closeLoadingPopup();
        }

        switch (status) {
            /*成功(僅表示Response沒問題，資料是否正確需看result內容)*/
            case BaseGlobalConfig.SUCCESS:
                daoCallback.onDataResult(result);
                break;
            /*請求逾時*/
            case BaseGlobalConfig.TIMEOUT:
                callback.onTimeoutResult();
                break;
            /*Https 401錯誤碼*/
            case BaseGlobalConfig.HTTPS_ERROR_401:
                callback.onHttpsError401Result();
                break;
            /*Https 500錯誤碼*/
            case BaseGlobalConfig.HTTPS_ERROR_500:
                callback.onHttpsError500Result();
                break;
            /*其他錯誤*/
            case BaseGlobalConfig.OTHER_ERROR:
                callback.onHttpsOtherErrorResult();
                break;
        }
    }

    /**
     * 多執行緒同時發出請求時，只顯示一次popup
     */
    private void showLoadingPopup(int message) {
        Context context = BaseGlobalFunction.getActivity();

        if (context != null){
            if (!((Activity) context).isFinishing()) {
                basePopupUtil.showLoadingPopup(context, context.getString(message));
            }
        }
    }

    /*Https憑證處理*/
    @SuppressLint("TrustAllX509TrustManager")
    private TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    };

    /*接受任意域名服務器*/
    private HostnameVerifier hnv = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}