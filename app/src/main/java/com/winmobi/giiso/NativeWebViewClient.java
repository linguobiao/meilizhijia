package com.winmobi.giiso;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by LW on 2016/9/12.
 */

public class NativeWebViewClient extends WebViewClient {

    public NativeWebViewClient(){
        super();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //Logger.e("onPageStarted ReqURL:" + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        //Logger.e("onPageFinished ReqURL:" + url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //设置不跳转打开URL
        view.loadUrl(url);
        return true;
    }

}
