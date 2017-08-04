package com.winmobi.global;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by linguobiao on 16/8/18.
 */
public class MyApp extends Application {
    private static MyApp myApp;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        //JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        initFresco();

    }
    public static MyApp getApp() {
        return myApp;
    }

    /**
     * 初始化Fresco图片框架
     */
    private void initFresco() {
        ProgressiveJpegConfig pjpegConfig = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                return scanNumber + 2;
            }

            public QualityInfo getQualityInfo(int scanNumber) {
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(pjpegConfig)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this,config);
    }
}
