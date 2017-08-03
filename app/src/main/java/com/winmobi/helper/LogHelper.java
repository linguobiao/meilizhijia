package com.winmobi.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.winmobi.global.Global;

/**
 * 
 * 带日志文件输入的，又可控开关的日志调试
 * 
 * 
 * 
 * @author Dsw
 * 
 * @version 1.0
 * 
 * @data 2012-2-20
 */

public class LogHelper {

	private static char LOG_TYPE = 'v'; // 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

	/**
	 * whether write system log
	 */
	private static Boolean LOG_SWITCH = true; // 日志文件总开关

	/**
	 * whether write log to file
	 */
	private static Boolean LOG_WRITE_TO_FILE = false; // 日志写入文件开关

	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0; // sd卡中日志文件的最多保存天数

	private static String LOGFILENAME = "jqrLog.txt"; // 本类输出的日志文件名称

	/**
	 * log file path
	 */
	private static String LOG_PATH_SDCARD_DIR = Global.LOG_PATH; // 日志文件在sdcard中的路径

	private static SimpleDateFormat LogSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss"); // 日志的输出格式

	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd"); // 日志文件格式

	public static void w(String tag, String text, String name) {

		log(tag, text, 'w', name);

	}

	public static void e(String tag, String text, String name) {

		log(tag, text, 'e', name);

	}

	public static void d(String tag, String text, String name) {

		log(tag, text, 'd', name);

	}

	public static void i(String tag, String text, String name) {

		log(tag, text, 'i', name);

	}

	public static void v(String tag, String text, String name) {

		log(tag, text, 'v', name);

	}

	/**
	 * 
	 * write log
	 * 
	 * 
	 * 
	 * @param tag
	 * 
	 * @param msg
	 * 
	 * @param level
	 * 
	 * @return void
	 * 
	 *         [url=home.php?mod=space&uid=66080]@SINCE[/url] v 1.0
	 */

	private static void log(String tag, String msg, char level, String name) {

		if (LOG_SWITCH) {

			if ('i' == level) {

				Log.e(tag, msg);

			} else if ('e' == level) {

				Log.i(tag, msg);

			} else if ('w' == level) {

				Log.w(tag, msg);

			} else if ('d' == level) {

				Log.d(tag, msg);

			} else {

				Log.v(tag, msg);

			}

			if (LOG_WRITE_TO_FILE)

				writeLogtoFile(String.valueOf(level), tag, msg, name);

		}

	}

	/**
	 * 
	 * write log to file
	 * 
	 * 
	 * 
	 * @return
	 * 
	 * **/

	private static synchronized void writeLogtoFile(String mylogtype,
			String tag, String text, String name) {

		Date nowtime = new Date();

//		String needWriteFiel = logfile.format(nowtime);

		String needWriteMessage = LogSdf.format(nowtime) + " " + mylogtype
				+ " " + tag + " " + text;

		File f = new File(LOG_PATH_SDCARD_DIR);
		if (!f.exists()) {
			f.mkdirs();
		}

		File file = new File(LOG_PATH_SDCARD_DIR, name);

		try {

			FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖

			BufferedWriter bufWriter = new BufferedWriter(filerWriter);

			bufWriter.write(needWriteMessage);

			bufWriter.newLine();

			bufWriter.close();

			filerWriter.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * delete log
	 * 
	 * */

	public static void delFile() {

		String needDelFiel = logfile.format(getDateBefore());

		File file = new File(LOG_PATH_SDCARD_DIR, needDelFiel + LOGFILENAME);

		if (file.exists()) {

			file.delete();

		}

	}

	/**
	 * 
	 * get limit date
	 * 
	 * */

	private static Date getDateBefore() {

		Date nowtime = new Date();

		Calendar now = Calendar.getInstance();

		now.setTime(nowtime);

		now.set(Calendar.DATE, now.get(Calendar.DATE)
				- SDCARD_LOG_FILE_SAVE_DAYS);

		return now.getTime();

	}

}
