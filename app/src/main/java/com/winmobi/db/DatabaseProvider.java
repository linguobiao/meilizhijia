package com.winmobi.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.winmobi.bean.JPush;
import com.winmobi.helper.FormatHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseProvider {

	
	/**
	 * 插入jpush
	 */
	public static void insertJpush(Context context, int profileID, JPush jpush) {
		if (jpush != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();
			Cursor cursor = databaseAdapter.query_jpush(profileID, jpush.getNotificationId());

			try {
				if (cursor.moveToFirst()) {
					databaseAdapter.update_jpush(profileID, jpush);
				} else {
					databaseAdapter.insert_jpush(profileID, jpush);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			databaseAdapter.closeDatabase();
		}
	}
	
	/**
	 * 更新jpush
	 */
	public static void updateJpush(Context context, int profileID, JPush jPush) {
		if (jPush != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();

			databaseAdapter.update_jpush(profileID, jPush);

			databaseAdapter.closeDatabase();
		}
	}

	/**
	 * 查询一个时刻的心率
	 */
	public static JPush queryJpush(Context context,int profileID,  int notificationId) {

		if (context != null) {

			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			// 打开数据库
			databaseAdapter.openDatabase();

			Cursor cursor = databaseAdapter.query_jpush(profileID, notificationId);
			try {
				if (cursor.moveToFirst()){

					JPush jPush = new JPush();
					Date _date = new Date();
					try {
						_date = FormatHelper.sdf_yyyy_MM_dd_HH_mm_ss.parse(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATETIME)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Calendar _cal = Calendar.getInstance();
					_cal.setTime(_date);

					jPush.setDate(_cal);
					jPush.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_TITLE)));
					jPush.setContent(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_CONTENT)));
					jPush.setExtras(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_EXTRAS)));
					jPush.setType(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_TYPE)));
					jPush.setFileHtml(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_HTML)));
					jPush.setFile(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_FILE)));
					jPush.setNotificationId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_NOTIFICATION_ID)));

					databaseAdapter.closeDatabase();
					return jPush;
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}

			databaseAdapter.closeDatabase();
		}

		return null;
	}

	/**
	 * 查询jpush
	 */
	public static List<JPush> queryJpush(Context context,int profileID, Calendar begin, Calendar end) {

		List<JPush> jPushList = new ArrayList<JPush>();
		
		if (context != null && begin != null && end != null) {
			begin = FormatHelper.setDayFormat(begin);
			end = FormatHelper.setDayFormat(end);
			
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			// 打开数据库
			databaseAdapter.openDatabase();
			SQLiteDatabase db = databaseAdapter.getSQLiteDatabase();
			// 开始事务
			db.beginTransaction();
			Cursor cursor;
			cursor = databaseAdapter.query_jpush_asc(profileID, begin, end, 61);

			try {
				if (cursor.moveToFirst()){
					do{
						JPush jPush = new JPush();
						Date _date = new Date();
						try {
							_date = FormatHelper.sdf_yyyy_MM_dd_HH_mm_ss.parse(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATETIME)));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						Calendar _cal = Calendar.getInstance();
						_cal.setTime(_date);
						
						jPush.setDate(_cal);
						jPush.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_TITLE)));
						jPush.setContent(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_CONTENT)));
						jPush.setExtras(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_EXTRAS)));
						jPush.setType(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_TYPE)));
						jPush.setFileHtml(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_HTML)));
						jPush.setFile(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_FILE)));
						jPush.setNotificationId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_JPUSH_NOTIFICATION_ID)));
						jPushList.add(jPush);
					} while(cursor.moveToNext());
					
				} 
				// 事务成功，提交事务
				db.setTransactionSuccessful();
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
				// 结束事务
				db.endTransaction();
				databaseAdapter.closeDatabase();
			}
			
			databaseAdapter.closeDatabase();
		}
		return jPushList;
	}
}
