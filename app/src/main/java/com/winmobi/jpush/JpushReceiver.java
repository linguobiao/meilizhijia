package com.winmobi.jpush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.winmobi.activity.HomeActivity;
import com.winmobi.activity.WechatLoadActivity;
import com.winmobi.bean.JPush;
import com.winmobi.db.DatabaseProvider;
import com.winmobi.global.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			saveJpush(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			saveJpush(context, bundle);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			clickMsg(context);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	public void saveJpush(Context context, Bundle bundle) {
		JPush jPush = new JPush();
		Calendar cal = Calendar.getInstance();
		jPush.setDate(cal);
		jPush.setNotificationId(bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
		jPush.setTitle(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
		jPush.setContent(bundle.getString(JPushInterface.EXTRA_ALERT));
		jPush.setExtras(bundle.getString(JPushInterface.EXTRA_EXTRA));
		jPush.setType(bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE));
		jPush.setFileHtml(bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH));
		jPush.setFile(bundle.getString(JPushInterface.EXTRA_MSG_ID));

		DatabaseProvider.insertJpush(context, Global.PROFILE_ID, jPush);
	}

	public void clickMsg(Context context) {
		Global.isShowMsg = true;
		SharedPreferences sp = context.getSharedPreferences("winmobi",Context.MODE_PRIVATE);
		String unionID=sp.getString(Global.RESPONSE_unionid,null);
		boolean isNeedUrlLoad=sp.getBoolean(Global.isNeedUrlLoad,false);
		System.out.println("winmobi-----isNeedUrlLoad:" + isNeedUrlLoad);
		if (isNeedUrlLoad || Global.RESPONSE_NowGoToUrl!=""){
			if (unionID==null) {
				Intent intent = new Intent(context, HomeActivity.class);
				intent.putExtra("url", sp.getString(Global.RESPONSE_NowGoToUrl, null));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}else {
				Intent intent = new Intent(context, HomeActivity.class);
				intent.putExtra("url",sp.getString(Global.RESPONSE_HomeUrl, null)+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}else {
			System.out.println("winmobi-----unionID:" + unionID);
			if (unionID == null) {
				Intent intent = new Intent(context, WechatLoadActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				Intent intent = new Intent(context, HomeActivity.class);
				intent.putExtra("url",sp.getString(Global.RESPONSE_HomeUrl, null)+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}

	}



}
