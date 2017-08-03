package com.winmobi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.winmobi.bean.JPush;
import com.winmobi.global.Global;
import com.winmobi.helper.FormatHelper;
import java.util.Calendar;

public class DatabaseAdapter {
	private static String TAG = "DatabaseAdapter";
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = Global.NAME_DB;
	////////////////////////////////////////////////////////////

	public static final String TABLE_JPUSH = "TABLE_JPUSH";
	
	///////////////////////////////////////////////////////////
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PROFILE_ID = "KEY_PROFILE_ID";
	public static final String KEY_DATE = "KEY_DATE";
	public static final String KEY_DATETIME = "KEY_DATETIME";
	public static final String KEY_DATETIME_LONG = "KEY_DATETIME_LONG";
	public static final String KEY_JPUSH_TITLE = "KEY_JPUSH_TITLE";
	public static final String KEY_JPUSH_CONTENT = "KEY_JPUSH_CONTENT";
	public static final String KEY_JPUSH_EXTRAS = "KEY_JPUSH_EXTRAS";
	public static final String KEY_JPUSH_NOTIFICATION_ID = "KEY_JPUSH_NOTIFICATION_ID";
	public static final String KEY_JPUSH_TYPE = "KEY_JPUSH_TYPE";
	public static final String KEY_JPUSH_HTML = "KEY_JPUSH_HTML";
	public static final String KEY_JPUSH_FILE = "KEY_JPUSH_FILE";

	///////////////////////////////////////////////////////////
	
	public static final String CREATE_TABLE_JPUSH = "CREATE TABLE IF NOT EXISTS " + TABLE_JPUSH +
			"(" + KEY_ROWID + " integer primary key autoincrement, " +
			KEY_DATE + " text not null, " + 
			KEY_DATETIME + " text not null, " + 
			KEY_DATETIME_LONG + " long not null, " +
			KEY_JPUSH_TITLE + " text not null, " +
			KEY_JPUSH_CONTENT + " text, " +
			KEY_JPUSH_EXTRAS + " text, " +
			KEY_JPUSH_TYPE + " text, " +
			KEY_JPUSH_HTML + " text, " +
			KEY_JPUSH_FILE + " text, " +
			KEY_JPUSH_NOTIFICATION_ID + " int not null, " +
			KEY_PROFILE_ID + " int not null" +
			"); ";
		
	//////////////////////////////////////////////////////////
		
	private final Context context;
	private DatabaseOpenHelper databaseOpenHelper;
	private SQLiteDatabase db;

	public DatabaseAdapter(Context ctx){
		this.context = ctx;
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * open database
	 * @return
	 */
	public DatabaseAdapter openDatabase(){
		if (databaseOpenHelper != null) {
			db = databaseOpenHelper.getWritableDatabase();
		}
		return this;
	}
	
	public SQLiteDatabase getSQLiteDatabase() {
		return db;
	}
	/**
	 * close database
	 */
	public void closeDatabase(){
		databaseOpenHelper.close();
	}
	
	
	/**
	 * 插入jpush
	 */
	public long insert_jpush(int profileID, JPush jPush) {
		Log.i("db", "insert jpush " + jPush.getTitle());
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE, FormatHelper.sdf_yyyy_MM_dd.format(jPush.getDate().getTime()));
		initialValues.put(KEY_DATETIME, FormatHelper.sdf_yyyy_MM_dd_HH_mm_ss.format(jPush.getDate().getTime()));
		initialValues.put(KEY_DATETIME_LONG, jPush.getDate().getTimeInMillis());
		initialValues.put(KEY_JPUSH_TITLE, jPush.getTitle());
		initialValues.put(KEY_JPUSH_CONTENT, jPush.getContent());
		initialValues.put(KEY_JPUSH_EXTRAS, jPush.getExtras());
		initialValues.put(KEY_JPUSH_TYPE, jPush.getType());
		initialValues.put(KEY_JPUSH_HTML, jPush.getFileHtml());
		initialValues.put(KEY_JPUSH_FILE, jPush.getFile());
		initialValues.put(KEY_JPUSH_NOTIFICATION_ID, jPush.getNotificationId());
		initialValues.put(KEY_PROFILE_ID, profileID);

		return db.insert(TABLE_JPUSH, null, initialValues);
	}
	
	
	/**
	 * 更新jpush
	 */
	public int update_jpush(int profileID, JPush jPush) {
		Log.i("db", "update jpush " + jPush.getTitle());
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE, FormatHelper.sdf_yyyy_MM_dd.format(jPush.getDate().getTime()));
		initialValues.put(KEY_DATETIME, FormatHelper.sdf_yyyy_MM_dd_HH_mm_ss.format(jPush.getDate().getTime()));
		initialValues.put(KEY_DATETIME_LONG, jPush.getDate().getTimeInMillis());
		initialValues.put(KEY_JPUSH_TITLE, jPush.getTitle());
		initialValues.put(KEY_JPUSH_CONTENT, jPush.getContent());
		initialValues.put(KEY_JPUSH_EXTRAS, jPush.getExtras());
		initialValues.put(KEY_JPUSH_TYPE, jPush.getType());
		initialValues.put(KEY_JPUSH_HTML, jPush.getFileHtml());
		initialValues.put(KEY_JPUSH_FILE, jPush.getFile());
		return db.update(TABLE_JPUSH,
				initialValues, 
				KEY_JPUSH_NOTIFICATION_ID + "=" + jPush.getNotificationId() + " and " + KEY_PROFILE_ID + "=" + profileID,
				null);
	}
	
	/**
	 * 查询jpush
	 */
	public Cursor query_jpush(int profileID, int notificationId) {
		Cursor mCursor = db.query(true, TABLE_JPUSH,
				new String[]{KEY_DATETIME, KEY_JPUSH_TITLE, KEY_JPUSH_CONTENT, KEY_JPUSH_EXTRAS, KEY_JPUSH_TYPE, KEY_JPUSH_HTML, KEY_JPUSH_FILE},
				KEY_JPUSH_NOTIFICATION_ID + "='" + notificationId + "' and " + KEY_PROFILE_ID + "=" + profileID,
				null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * desc 是descend 降序意思  asc 是ascend 升序的意思
	 * @param profileID
	 * @param limit
	 * @return
	 */
	public Cursor query_jpush_asc(int profileID, Calendar dateBegin, Calendar dateEnd, int limit) {

		Log.i("db", "query jpush asc ");
		
		Cursor mCursor = db.query(true, TABLE_JPUSH,
				new String[]{KEY_DATETIME, KEY_JPUSH_TITLE, KEY_JPUSH_CONTENT, KEY_JPUSH_EXTRAS, KEY_JPUSH_TYPE, KEY_JPUSH_HTML, KEY_JPUSH_FILE, KEY_JPUSH_NOTIFICATION_ID},
				KEY_DATETIME_LONG + ">=" + dateBegin.getTimeInMillis() + " and " + KEY_DATETIME_LONG + "<" + dateEnd.getTimeInMillis()  + " and " + KEY_PROFILE_ID + "=" + profileID,
				null, null, null, KEY_DATETIME + " desc", String.valueOf(limit));
		
		return mCursor;
	}
	
	/**
	 * desc 是descend 降序意思  asc 是ascend 升序的意思
	 * @param profileID
	 * @return
	 */
	public Cursor query_jpush(int profileID, Calendar dateBegin, Calendar dateEnd) {
		
		Cursor mCursor = db.query(true, TABLE_JPUSH,
				new String[]{KEY_DATETIME, KEY_JPUSH_TITLE, KEY_JPUSH_CONTENT, KEY_JPUSH_EXTRAS, KEY_JPUSH_TYPE, KEY_JPUSH_HTML, KEY_JPUSH_FILE, KEY_JPUSH_NOTIFICATION_ID},
				KEY_DATETIME_LONG + ">=" + dateBegin.getTimeInMillis() + " and " + KEY_DATETIME_LONG + "<" + dateEnd.getTimeInMillis()  + " and " + KEY_PROFILE_ID + "=" + profileID,
				null, null, null, null, null);
		
		return mCursor;
	}
}
