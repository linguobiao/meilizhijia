package com.xyuntuanwinmobi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.winmobi.global.Global;
import com.winmobi.helper.LogHelper;

/**
 * Created by luocan on 2016/7/27.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Global.WechatAppId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d("winmobi-----", "onPayFinish, errCode = " + baseResp.errCode);
        int errCode = baseResp.errCode;
        LogHelper.d(Global.TAG,"支付---》最终结果，errCode = " + errCode,Global.NAME_LOG);
        if (errCode == 0){
            Intent intent=new Intent(Global.ACTION_PAY_SUCCESS);
            sendBroadcast(intent);
            finish();
        }else if (errCode == -1){
            Intent intent=new Intent(Global.ACTION_PAY_FAIL);
            sendBroadcast(intent);
            finish();
        }else if (errCode == -2){
            Intent intent=new Intent(Global.ACTION_PAY_FAIL);
            sendBroadcast(intent);
            finish();
        }
    }
}
