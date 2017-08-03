package com.winmobi.giiso;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by LW on 2017/4/5.
 */

public class NativeApiForH5 {

    public static final String INTER_FACE_NAME = "NativeApiForH5";
    public static final String BACKFORWEB = "BACKFORWEB";
    public static final String WEB_VIEW_PIC_WIDTH = "WEB_VIEW_PIC_WIDTH";
    public static final String WEB_VIEW_PIC_HEIGHT = "WEB_VIEW_PIC_HEIGHT";


    public static void callBack(WebView webView) {
        if (webView.getContext() != null){
            ((Activity)webView.getContext()).finish();
        }else {
            Toast.makeText(webView.getContext(), "当前无法返回", Toast.LENGTH_LONG).show();
        }
    }


    public static final String BROADCAST_RECEIVER_CHART = "com.crm.wdsoft.web.initFinished.BROADCAST";
    public static final String HTML_FINISHED_FLAG = "HTML_FINISHED_FLAG";
    public static void htmlInitFinishedCallBack(WebView webView,String type){
        if(TextUtils.isEmpty(type)) return;
        Intent intent = new Intent(BROADCAST_RECEIVER_CHART);
        intent.putExtra("HTML_FINISHED_FLAG",type);
        webView.getContext().sendBroadcast(intent);
    }

}
