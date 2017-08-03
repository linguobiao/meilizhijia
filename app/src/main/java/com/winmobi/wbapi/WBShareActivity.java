package com.winmobi.wbapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.winmobi.R;
import com.winmobi.global.Global;

/**
 * Created by luocan on 2016/8/2.
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response {

    private IWeiboShareAPI iWeiboShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebo();
        if (savedInstanceState != null) {
            iWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        iWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if(baseResponse!= null){
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(this, getString(R.string.share_ok), Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(this,getString(R.string.share_fail) , Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(this,
                            getString(R.string.share_fail) + "  Error Message: " + baseResponse.errMsg,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
        finish();
    }

    private void initWebo() {
        iWeiboShareAPI= WeiboShareSDK.createWeiboAPI(this, Global.WechatAppId);
        iWeiboShareAPI.registerApp();
    }
}
