package com.winmobi.giiso;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.winmobi.utils.ScreenUtils;

import java.util.List;

/**webview基类，为了统一配置webview的相关属性
 * Created by Administrator on 2016/12/24.
 */

public class GiisoWebView extends WebView {
    private OnScrollChangedListener mScrollChangedListener;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private List<View> mViews;
    private boolean mVisible = true;

    public GiisoWebView(Context context) {
        super(context);
        init();
    }

    public GiisoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiisoWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("SetJavaScriptEnabled")
    private void init(){
        WebSettings settings = getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT > 16){//设置视频自动播放
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持js自动弹框
        settings.setSupportZoom(true); //支持缩放
        settings.setBuiltInZoomControls(true); //支持手势缩放
        settings.setDisplayZoomControls(false); //是否显示缩放按钮
        // >= 19(SDK4.4)启动硬件加速，否则启动软件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            settings.setLoadsImagesAutomatically(false);
        }
        settings.setUseWideViewPort(true); //设置内容自适应屏幕大小
        settings.setLoadWithOverviewMode(true);
        settings.setDatabaseEnabled(true);//设置允许使用数据库
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);//允许使用缓存
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(true);
        settings.setSupportMultipleWindows(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); //优先使用缓存
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER); // 取消WebView中滚动或拖动到顶部、底部时的阴影
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // 取消滚动条白边效果
        requestFocus();

        //注意：以下两项是必须设置，其他设置选项根据自身项目需求进行配置
        settings.setJavaScriptEnabled(true); //必须设置
        setWebViewClient(new WebViewClient());

        //移除Android系统内部的默认内置接口,存在远程代码执行漏洞
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
    }

    /**
     * 监控webview滑动，以判断滑动方向
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mViews != null){
            if (t-oldt > 0){//上拉   隐藏返回键
                if (mVisible){
                    mVisible = false;
                    scrollViews(false);
                }
            }else{//下拉  显示返回键
                if (!mVisible){
                    mVisible = true;
                    scrollViews(true);
                }
            }
        }
        if (mScrollChangedListener != null){
            mScrollChangedListener.onScrollChanged(l,t,oldl,oldt);
        }
    }

    /**
     * 页面底部的控制栏随着webview滑动而显示或隐藏
     * @param isShow
     */
    private void scrollViews(boolean isShow){
        if (isShow){//显示
            for (View view :mViews) {
                view.animate().setInterpolator(mInterpolator)
                        .setDuration(200)
                        .translationY(0).start();
            }
        }else{//隐藏
            for (View view :mViews) {
                view.animate().setInterpolator(mInterpolator)
                        .setDuration(200)
                        .translationY(view.getHeight() + ScreenUtils.dp2px(this.getContext(),20)).start();
            }
        }
    }

    /**
     * 给WebView设置滑动监听
     * @param listener
     */
    public void setOnScrollChangedListener(OnScrollChangedListener listener){
        mScrollChangedListener = listener;
    }

    public interface  OnScrollChangedListener{
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public void setViewsToFollowScroll(List<View> views){
        mViews = views;
    }
}
