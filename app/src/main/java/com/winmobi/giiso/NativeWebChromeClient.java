package com.winmobi.giiso;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.WeakHashMap;

/**
 * Created by zhao_king on 2016/9/18.
 */

public class NativeWebChromeClient extends WebChromeClient {
    public static final int FILECHOOSER_RESULTCODE = 541;
    public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 542;
    public static final int FILECHOOSER_RESULTCODE_CROP = 543;

    private Activity context;
    private ProgressBar progressBar;
    private JsCallJava mJsCallJava;

    private WeakHashMap<String,Integer> refeshMap;
    public NativeWebChromeClient(Activity context, ProgressBar progressBar) {
        mJsCallJava = new JsCallJava(NativeApiForH5.INTER_FACE_NAME,NativeApiForH5.class);
        this.progressBar = progressBar;
        this.context = context;
//        h5_title_flase = context.getResources().getStringArray(R.array.h5_title_flase);
        refeshMap = new WeakHashMap<>();
    }

    private String[] h5_title_flase;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (progressBar != null){
            if (newProgress == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                if (View.INVISIBLE == progressBar.getVisibility()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
            }
        }

        //为什么要在这里注入JS
        //1 OnPageStarted中注入有可能全局注入不成功，导致页面脚本上所有接口任何时候都不可用
        //2 OnPageFinished中注入，虽然最后都会全局注入成功，但是完成时间有可能太晚，当页面在初始化调用接口函数时会等待时间过长
        //3 在进度变化时注入，刚好可以在上面两个问题中得到一个折中处理
        //为什么是进度大于25%才进行注入，因为从测试看来只有进度大于这个数字页面才真正得到框架刷新加载，保证100%注入成功
        //if (newProgress <= 25) {
        //    mIsInjectedJS = false;
        //} else if (!mIsInjectedJS) {
        view.loadUrl(mJsCallJava.getPreloadInterfaceJS());
        //   mIsInjectedJS = true;
        //Logger.d(" inject js interface completely on progress " + newProgress);
        //}
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if(TextUtils.isEmpty(title) || null == h5_title_flase) return;
        // 当遇到上述情况的时候，默认刷新5次，如果5次内还存在此情况，则停止自动刷新
        for (String t : h5_title_flase) {
            if (t.equalsIgnoreCase(title) || t.contains(title)){
                if(refeshMap.containsKey(view.getUrl())) {
                    if(refeshMap.get(view.getUrl()) >= 5) {
                        refeshMap.clear();
                        return;
                    }
                    refeshMap.put(view.getUrl(),refeshMap.get(view.getUrl())+1);
                }else{
                    refeshMap.put(view.getUrl(),1);
                }
                //Toast.makeText(context,view.getUrl()+"=="+title+"=="+refeshMap.get(view.getUrl()),Toast.LENGTH_LONG).show();
                view.reload();
                return;
            }
        }
    }

    // 处理Alert事件
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        result.confirm();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        result.confirm(mJsCallJava.call(view, message));
        return true;
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        //Log.d("UPFILE", "in openFile Uri Callback");
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        context.startActivityForResult(Intent.createChooser(i, "选择文件"), FILECHOOSER_RESULTCODE);
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        //Log.d("UPFILE", "in openFile Uri Callback has accept Type" + acceptType);
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        String type = TextUtils.isEmpty(acceptType) ? "image/*" : acceptType;
        i.setType(type);
        context.startActivityForResult(Intent.createChooser(i, "选择文件"),FILECHOOSER_RESULTCODE);
    }

    // For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        //Log.d("UPFILE", "in openFile Uri Callback has accept Type" + acceptType + "has capture" + capture);
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        String type = TextUtils.isEmpty(acceptType) ? "image/*" : acceptType;
        i.setType(type);
        context.startActivityForResult(Intent.createChooser(i, "选择文件"), FILECHOOSER_RESULTCODE);
    }

    //Android 5.0+
    //@Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        //Log.d("UPFILE", "file chooser params：" + fileChooserParams.toString());
        if (mUploadMessageForAndroid5 != null) {
            mUploadMessageForAndroid5.onReceiveValue(null);
        }
        mUploadMessageForAndroid5 = filePathCallback;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, i);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择文件");

        context.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
        return true;
    }

}