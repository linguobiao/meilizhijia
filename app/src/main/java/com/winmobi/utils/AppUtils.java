package com.winmobi.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * 跟App相关的辅助类
 * @author hepf
 */
public class AppUtils {

	private AppUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [智能版传给服务端的应用程序版本名称信息，需要在应用版本名前加“Z”]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return "Z" + packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 *
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取Manifest中application结点下的meta数据
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static String getApplicationMetaData(Context context, String name) {

		Object result = "";
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			result = info.metaData.get(name);
			if(result == null){ result = ""; }
			System.out.println("application meta: key = " + name + ", value = "
					+ result.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 *判断应用是否在后台
	 */
	final public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
			}
		}
		return false;
	}

	/**
	 *判断应用是否在后台
	 */
	final public static boolean isProcessRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 获取Manifest中application结点下的meta数据
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getApplicationMetaDataInt(Context context, String name) {

		int result = 0;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			result = info.metaData.getInt(name);
			System.out.println("application meta: key = " + name + ", value = "
					+ result);			
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}
	
	/**
	 * 获取Manifest中application结点下的meta数据
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static long getApplicationMetaDataLong(Context context, String name) {

		long result = 0;
		ApplicationInfo info = null;
		try {
			
			info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			result = info.metaData.getLong(name);
			System.out.println("application meta: key = " + name + ", value = "
					+ result);			
		}catch (NameNotFoundException e1) {
			result = 0;
		}catch (Exception e) {
			if(info != null){ 
				result = info.metaData.getInt(name);
			}
		}
		return result;
	}
	
	/**
	 * [获取应用程序版本编号]
	 * 
	 * @param context
	 * @return 当前应用的版本编号
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获取手机imei
	 * @param context
	 * @return
	 */
	public static String getImei(Context context){
		try{
			String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			return TextUtils.isEmpty(imei) ? getSecondImei() : imei;
		}catch (Exception e){
			return getSecondImei();
		}
	}

	/**
	 * 获取imei失败时，构造一串类似于imei号的字符串
	 * @return
     */
	private static String getSecondImei(){
		String ANDROID_ID = "";
		try {
			ANDROID_ID  = "35" + //we make this look like a valid IMEI
					Build.BOARD.length()%10 +
					Build.BRAND.length()%10 +
					Build.CPU_ABI.length()%10 +
					Build.DEVICE.length()%10 +
					Build.DISPLAY.length()%10 +
					Build.HOST.length()%10 +
					Build.ID.length()%10 +
					Build.MANUFACTURER.length()%10 +
					Build.MODEL.length()%10 +
					Build.PRODUCT.length()%10 +
					Build.TAGS.length()%10 +
					Build.TYPE.length()%10 +
					Build.USER.length()%10 ; //13 digits
		}catch (Exception e){
			ANDROID_ID = java.util.UUID.randomUUID().toString();
		}
		return ANDROID_ID;
	}

	private static String intToIp(int ip){
		return (ip & 0xFF) + "." + 
			((ip >> 8) & 0xFF) + "." +
			((ip >> 16) & 0xFF) + "." +
			((ip >> 24) & 0xFF) ;
	}
	
	/**
	 * 使用GPRS获取ip
	 * @return
	 */
	private static String getLocalIpAddress()  
    {  
        try  
        {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)  
            {  
               NetworkInterface intf = en.nextElement();  
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)  
               {  
                   InetAddress inetAddress = enumIpAddr.nextElement();  
                   if (!inetAddress.isLoopbackAddress())  
                   {  
                       return inetAddress.getHostAddress().toString();  
                   }  
               }  
           }  
        }  
        catch (SocketException ex)  
        {  
        }
        return null;  
    }  
	
	/**
	 *  得到本机Mac地址
	 * @return
	 */
    public static String getLocalMac(Context context)
    {
        // 获取wifi管理器
        WifiManager wifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        
        return wifiInfor.getMacAddress();
    }
	
	/**
	 * 获取手机品牌型号
	 * @return
	 */
    public static String getPhoneModel(){
    	return Build.MODEL;
    }

	/**
	 * 获取手机系统版本
	 * @return
	 */
	public static int getSystemVersion(){
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 判断某个服务是否正在运行的方法
	 *
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	/**
	 * 获取当前进程名
	 * @param cxt
	 * @param pid
     * @return
     */
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}

	/**
	 * SD卡判断
	 *
	 * @return boolean
	 */
	public static boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建App文件夹
	 *
	 * @param appName appName
	 * @param application application
	 * @return String
	 */
	public static String createAPPFolder(String appName, Application application) {
		return createAPPFolder(appName, application, null);
	}


	/**
	 * 创建App文件夹
	 *
	 * @param appName appName
	 * @param application application
	 * @param folderName folderName
	 * @return String
	 */
	public static String createAPPFolder(String appName, Application application, String folderName) {
		File root;
		File folder;
		/**
		 * 如果存在SD卡
		 */
		if (isSDCardAvailable()) {
			root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			folder = new File(root, appName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		} else {
			/**
			 * 不存在SD卡，就放到缓存文件夹内
			 */
			root = application.getCacheDir();
			folder = new File(root, appName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
		if (folderName != null) {
			folder = new File(folder, folderName);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
		return folder.getAbsolutePath();
	}



}
