package com.winmobi.giiso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;

/**
 * 注入到WebView中的java对象
 */
public class NewsJavascriptInterface {
    private static final String TAG = "NewsJavascriptInterface";
    public static final String NAME = "JSInterface";
    private WeakReference<Context> mContext;

    public static NewsJavascriptInterface with(@NonNull Context context, OnJsListener onJsListener){
        return new NewsJavascriptInterface(context, onJsListener);
    }

    private NewsJavascriptInterface(@NonNull Context context, OnJsListener onJsListener) {
        mContext = new WeakReference<>(context);
        this.onJsListener = onJsListener;
    }

    /**
     * 打开新闻详情页，js调用,在新闻列表页点击单篇新闻时会回调该方法，把该新闻的相关信息回传
     * app端可以在该方法内跳转到新的activity，然后用webview加载详情的url，该新闻的一些必要字段都已提供
     * app端可以自由的扩展定制自身的业务
     * @param newsInfo  新闻相关信息的json
     *                    参数类型：json字符串
     *                    返回样例：{"title":"利比亚载118人飞机被劫 劫机者释放人质后被捕","detailUrl":"http://tj.giiso.com/wap/news53/index.do?version=Z1.0.3&newsFrom=recNews&clientFrom=false&mediaType=&newsId=AVkuRNbizC7k6JELX_kE&test=false&uid=992776&isShare=1&dark=false","source":"中国新闻网","time":"2016-12-24","imageUrls":[]}
     *                    说明：
     *                      title：新闻标题
     *                      detailUrl：h5新闻详情页的url
     *                      source：新闻来源
     *                      time：新闻发布时间
     *                      imageUrls：新闻图片url(jsonArray,可能有多张，也可能长度为0)
     *                      imageContents:新闻图片描述
     *                      type:新闻类型（0：普通新闻:1：图片新闻）
     */
    @JavascriptInterface
    public void openNewsDetailPage(String newsInfo) {
        if (TextUtils.isEmpty(newsInfo)){
            Log.e(TAG,"---h5端js调用所传参数为null----");
            return;
        }
        News news = JSON.parseObject(newsInfo,News.class);
        if (news != null && !TextUtils.isEmpty(news.getTitle())
                && !TextUtils.isEmpty(news.getDetailUrl())){
            if (mContext.get() != null){
                if (onJsListener != null) onJsListener.onNewsDetail(news);
            }
        }
    }

    /**
     * 新闻详情页里点击大图进入高清图浏览模式
     * js回传数据格式：{"position":"0","imgs":[{"content":"","simage":"http://img2.cache.netease.com/photo/0026/2017-01-03/C9S2VS173QLI0026.jpg","limage":"http://img2.cache.netease.com/photo/0026/2017-01-03/C9S2VS173QLI0026.jpg"}]}
     *  position: 新闻详情页里点击的第几张图片，如点击详情页里第一张图片则position为0
     *  content: 每张图片对应的简介，预留字段，现有版本该字段返回为空
     *  simage、limage: 图片链接
     */
    @JavascriptInterface
    public void openPictureView(String data) {
        if (TextUtils.isEmpty(data)){
            Log.e(TAG,"---openPictureView---h5传参为null----");
            return;
        }
        PictureBean bean = JSON.parseObject(data, PictureBean.class);

        if (bean != null && bean.getImgs() != null
                && bean.getPosition() > -1
                && bean.getPosition() < bean.getImgs().size()){
            if (mContext.get() != null){
                if (onJsListener != null) onJsListener.onOpenPicture(bean);
            }
        }
    }

    private OnJsListener onJsListener;
    public interface OnJsListener {
        void onNewsDetail(News news);
        void onOpenPicture(PictureBean bean);
    }

}
