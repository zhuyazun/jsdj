package com.sum.alchemist.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.sum.alchemist.Config;
import com.sum.xlog.core.XLog;
import com.tencent.mm.sdk.modelpay.PayReq;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * CommonUtil
 * Created by Qiu on 2016/6/1.
 */
public class CommonUtil {

    private static final String TAG = "CommonUtil";

    /**
     * List Map Object is Empty
     * @param object Object
     * @return boolean
     */
    public static boolean isEmpty(Object object){
        if(object instanceof List) {
            return ((List)object).size() < 1;
        }else if(object instanceof Map){
            return ((Map)object).size() < 1;
        }
        return  object == null;
    }



    /**
     * 隐藏名称
     */
    public static String hideName(String string){

        if(!TextUtils.isEmpty(string)){
            int length = string.length();
            if(length > 1) {
                string = string.substring(0, 1);
                string = string + (length > 2?"**":"*");
            }
        }
        return string;
    }

    public static String fomatPhone(String phone){
        if(phone == null || phone.length() != 11){
            return phone;
        }
        return phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
    }

    public static int optInt(String string){
        try {
            return Integer.valueOf(string);
        }catch (Exception e){
            XLog.e(TAG, "optInt", e);
        }

        return 0;
    }

    /**
     * 创建微信MD5加密串
     * @param payReq 支付参数
     * @return String
     */
    public static String getWechatSign(PayReq payReq){
        try {
            TreeMap<String, String> map = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            if (!TextUtils.isEmpty(payReq.appId))
                map.put("appid", payReq.appId);
            if (!TextUtils.isEmpty(payReq.partnerId))
                map.put("partnerid", payReq.partnerId);
            if (!TextUtils.isEmpty(payReq.prepayId))
                map.put("prepayid", payReq.prepayId);
            if (!TextUtils.isEmpty(payReq.packageValue))
                map.put("package", payReq.packageValue);
            if (!TextUtils.isEmpty(payReq.nonceStr))
                map.put("noncestr", payReq.nonceStr);
            if (!TextUtils.isEmpty(payReq.timeStamp))
                map.put("timestamp", payReq.timeStamp);

            StringBuilder stringBuilder = new StringBuilder();
            for (String key : map.keySet()) {
                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(map.get(key));
                stringBuilder.append("&");
            }
            Log.d(TAG, stringBuilder.toString());
            stringBuilder.append("key=" + Config.Common.WX_MD5);
            String result = MD5.getStringMD5(stringBuilder.toString()).toUpperCase();
            XLog.d(TAG, "Sign key = %s",result);
            return result;
        }catch (Exception e){
            XLog.e(TAG, "=== 微信加密串生成失败 ===", e);
            return "";
        }
    }


    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            XLog.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.isAvailable()) {
                    return true;
                }
            }
        }
        XLog.d(TAG, "network is not available");
        return false;
    }

    /**
     * 判断wifi 是否可用
     */
    public static boolean isWifiDataEnable(Context context) {
        boolean isWifiDataEnable = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            isWifiDataEnable = connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        }catch (Exception e){
            XLog.e(TAG, "=== 读取网络错误 ===", e);
        }
        return isWifiDataEnable;
    }

    public static String getSign(String username, long timestamp){
        String str = username + timestamp + "rwGiEaXrE2KgHimOPOkalRaWflH70QLd";
        str = MD5.getStringMD5(str) + "rwGiEaXrE2KgHimOPOkalRaWflH70QLd";
        return MD5.getStringMD5(str);
    }


    private static String downloadTaskUrl;
    public static void downloadServiceTask(Context context, String url){
        if(url.equals(downloadTaskUrl))
            return;
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        //开始下载
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setNotificationVisibility(View.VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir("/download/", "base.apk");
        request.setTitle("新版本下载");
        XLog.d(TAG, "开始下载 url %s", url);
        downloadManager.enqueue(request);
        downloadTaskUrl = url;
    }

}
