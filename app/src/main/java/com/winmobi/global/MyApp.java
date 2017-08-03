package com.winmobi.global;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;
import im.fir.sdk.FIR;

/**
 * Created by linguobiao on 16/8/18.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
