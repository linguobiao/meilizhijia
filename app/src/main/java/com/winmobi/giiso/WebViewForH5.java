package com.winmobi.giiso;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by zhao_king on 2016/9/22.
 */

public class WebViewForH5 extends WebView{
    private Activity activity;
    private ProgressBar progressBar;
    private NativeWebChromeClient webChromeClient;
    private NativeWebViewClient webViewClient;
    private WeakReference<Context> contextWeakReference;

    public WebViewForH5(Context context, AttributeSet attrs) {
        super(context, attrs);
        contextWeakReference = new WeakReference<>(context);

        this.removeJavascriptInterface("searchBoxJavaBridge_");
        this.removeJavascriptInterface("accessibility");
        this.removeJavascriptInterface("accessibilityTraversal");
    }

    //设置WebView
    public void setClient(){
        WebSettings settings = this.getSettings();
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //settings.setSupportZoom(true); //支持缩放//Zoom Control on web (You don't need this if ROM supports Multi-Touch
        //settings.setBuiltInZoomControls(true);//启用内置缩放装置
        saveData(settings);
        newWin(settings);
        //settings.setPluginState(WebSettings.PluginState.ON);
        // html5调用Android接口
        //CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        //cookieManager.removeSessionCookie();
        //cookieManager.removeAllCookie();
        if(getActivity() == null){
            Toast.makeText(contextWeakReference.get(), "需要先调用setActivity()!", Toast.LENGTH_SHORT).show();
        }else {
            setMyWebChromeClient(new NativeWebChromeClient(getActivity(), progressBar));
            setWebChromeClient(getWebChromeClient());
        }
        setMyWebViewClient(new NativeWebViewClient());
        setWebViewClient(getWebViewClient());

        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(this,true);
        }
    }

    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        mWebSettings.setDomStorageEnabled(true);
        //开启 数据库 存储功能
        //mWebSettings.setDatabaseEnabled(true);
        //设置数据库缓存路径,数据库默认生产地址
        // /data/data/package_name/database/webview.db
        // /data/data/package_name/database/webviewCache.db
        //mWebSettings.setDatabasePath(FileUtil.buildAppCachePath());
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setSavePassword(false);
        String appCachePath = contextWeakReference.get().getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public NativeWebChromeClient getWebChromeClient() {
        return webChromeClient;
    }

    public void setMyWebChromeClient(NativeWebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    public NativeWebViewClient getWebViewClient() {
        return webViewClient;
    }

    public void setMyWebViewClient(NativeWebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }
}
