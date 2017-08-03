package com.winmobi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	private static String TAG = "DatabaseOpenHelper";

	public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "******* sscreate table");
		db.execSQL(DatabaseAdapter.CREATE_TABLE_JPUSH);
		Log.i(TAG, "******* create table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "update database" + "      old version = " + oldVersion + "    new version = " + newVersion);

		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_JPUSH);

		db.execSQL(DatabaseAdapter.CREATE_TABLE_JPUSH);

	}

}
