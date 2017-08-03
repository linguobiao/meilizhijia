package com.winmobi.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShareHelper {
	private static String TAG = "ShareHelper";
	
	public static final String COM_TENCENT_MM_TIMELINE = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
	public static final String COM_TENCENT_MM_FREIND = "com.tencent.mm.ui.tools.ShareImgUI";
	public static final String COM_SINA_MFWEIBO_EDITACTIVITY = "com.sina.weibo.composerinde.ComposerDispatchActivity";
	public static final String COM_QZONE_UI = "com.qzonex.module.operation.ui.QZonePublishMoodActivity";
	public static final String COM_FACEBOOK = "com.facebook.composer.shareintent.ImplicitShareIntentHandler";
	public static final String COM_TWITTER = "com.twitter.android.composer.ComposerActivity";

	/**
	 * Checking for all possible internet providers
	 * **/
	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	/**
	 * 设置需要分享的应用程序
	 * 
	 * @param activity
	 * @return
	 */
	public static Map<String, ResolveInfo> setShareApp(Activity activity) {

		Map<String, ResolveInfo> mapResolveInfo = new LinkedHashMap<String, ResolveInfo>();
		PackageManager pManager = activity.getPackageManager();
		List<ResolveInfo> resolveInfos = getShareApp(activity);

		for (ResolveInfo resolveInfo : resolveInfos) {
			Log.i(TAG, resolveInfo.activityInfo.name + ", " + resolveInfo.loadLabel(pManager).toString());

			String packageName = resolveInfo.activityInfo.name;
			// 微信朋友圈
			if (packageName.startsWith(COM_TENCENT_MM_TIMELINE)) {
				mapResolveInfo.put(COM_TENCENT_MM_TIMELINE, resolveInfo);
			}
			// 微信朋友
			else if (packageName.startsWith(COM_TENCENT_MM_FREIND)) {
				mapResolveInfo.put(COM_TENCENT_MM_FREIND, resolveInfo);
			}
			// 新浪微博
			else if (packageName.startsWith(COM_SINA_MFWEIBO_EDITACTIVITY)) {
				mapResolveInfo.put(COM_SINA_MFWEIBO_EDITACTIVITY, resolveInfo);
			}
			// QQ 空间
			else if (packageName.startsWith(COM_QZONE_UI)) {
				mapResolveInfo.put(COM_QZONE_UI, resolveInfo);
			}
			// facebook
			else if (packageName.startsWith(COM_FACEBOOK)) {
				mapResolveInfo.put(COM_FACEBOOK, resolveInfo);
			}
			// Twitter
			else if (packageName.startsWith(COM_TWITTER)) {
				mapResolveInfo.put(COM_TWITTER, resolveInfo);
			}
		}
		return mapResolveInfo;
	}

	/**
	 * 获取手机里面所有支持分享功能的应用程序
	 *
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> getShareApp(Context context) {

		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/jpg");
		PackageManager pManager = context.getPackageManager();
		List<ResolveInfo> appList = pManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		return appList;
	}

//	public static void actionShare_sms_email_facebook(String packageName, Activity activity, Bitmap bmp, String shareText) {
//
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("image/jpg");
//		intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
//		intent.putExtra(Intent.EXTRA_TEXT, shareText);
//
//		// File file = new File(ScreenshotTools.filePath + File.separator + fileName);
//		intent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, bmp));
//		// intent.putExtra(Intent.EXTRA_STREAM, DatabaseProvider.queryScreenshot(activity, datetime.getTime()));
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setPackage(packageName);
//		activity.startActivity(intent);
//		System.out.println("****3");
//	}

//	/**
//	 * 分享到微信
//	 *
//	 * @param resolveInfo
//	 * @param activity
//	 * @param bmp
//	 * @param shareText
//	 */
//	public static void actionShare_wechat(ResolveInfo resolveInfo, Activity activity, Bitmap bmp, String shareText) {
//
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("image/*");
//		// intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
//
//		intent.putExtra(Intent.EXTRA_TEXT, shareText);
//
//		intent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, bmp));
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		// intent.setPackage(packageName);
//		// intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
//		ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
//		intent.setComponent(componentName);
//		activity.startActivity(intent);
//	}

	/**
	 * 分享默认
	 *
	 * @param resolveInfo
	 *            需要分享的应用的信息
	 * @param activity
	 *            分享图片
	 * @param shareText
	 *            分享文字
	 * @param title
	 * 			  分享标题
	 */
	public static void actionShare_default(ResolveInfo resolveInfo, Activity activity, String title, String shareUrl,String shareText,Bitmap bitmap) {

		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("text/plain");
//		String title = "标题";
//		String extraText="给大家介绍一个好网站，www.jcodecraeer.com";
//		intent.putExtra(Intent.EXTRA_TEXT, text);

		intent.setType("image/*");
		File tempdir = activity.getExternalFilesDir(null);

		Uri uri=getImageUri(activity, bitmap);
		System.out.println("winmobi-----uri:" + uri.getPath());
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.putExtra(Intent.EXTRA_TEXT, shareUrl+"	"+shareText);
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
//		intent.putExtra(Intent.EXTRA_STREAM, getImageUri(activity, bmp));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setPackage(resolveInfo.activityInfo.packageName);
		activity.startActivity(intent);
	}
	public static Uri getImageUri(Context inContext, Bitmap bmp) {
		Uri uri = null;
		FileHelper.newDir(inContext.getExternalCacheDir().getPath()+ "/IMG/");
		File ImgFile = new File(inContext.getExternalCacheDir().getPath()+ "/IMG/"+"WBshare.jpg");
		Log.i("winmobi-----", "imagefile = " + ImgFile.exists());
		if (ImgFile.exists()) {
			ImgFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(ImgFile);
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		uri = Uri.fromFile(ImgFile);
		Log.i("share", "uri = " + uri);
		return uri;
	}

}
