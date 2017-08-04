package com.winmobi.giiso;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.giiso.sdk.GiisoServiceListener;
import com.giiso.sdk.openapi.GiisoApi;
import com.giiso.sdk.openapi.GiisoServiceConfig;

/**
 * Created by LGB on 2017/8/3.
 */

public class GiisoManager {

    private final String uid = "101616";
    private String mUrl;
    private Context mContext;

    //单例
    private static class Instance {public static final GiisoManager instance = new GiisoManager();}
    public static GiisoManager getInstance() {
        return Instance.instance;
    }

    /**
     * 第一步：初始化SDK
     */
    public void initSDK(Context context, OnInitGiisoListener listener) {
        mContext = context;
        onInitGiisoListener = listener;
        //构造GiisoServiceConfig对象时，必须设置GiisoServiceListener回调接口，否则初始化不会成功
        GiisoServiceConfig config = GiisoServiceConfig.newBuilder()
                .setAppUid(uid)//设置appuid，app端自身的业务uid,可以不设
                .setGiisoServiceListener(new GiisoServiceListener() {//回调接口,必须设置
                    @Override
                    public void onSuccess(String webUrl, String uid, String token) {
                        //sdk初始化成功后回调，执行在主进程
                        mUrl = webUrl;
                        if (onInitGiisoListener != null) onInitGiisoListener.onSuccess(mUrl);
                    }

                    @Override
                    public void onError(String msg, int code) {
                        Toast.makeText(mContext, msg + ",  " + code, Toast.LENGTH_LONG).show();
                    }
                })
//                .setLocation(getLocation())//位置信息，建议设置，有利于为您推荐更精准的本地资讯
                .build();//构造config对象
        GiisoApi.init(mContext,config);//初始化sdk
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    private OnInitGiisoListener onInitGiisoListener;
    public interface OnInitGiisoListener {
        void onSuccess(String webUrl);
    }
}
