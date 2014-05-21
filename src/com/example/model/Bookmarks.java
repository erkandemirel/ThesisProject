package com.example.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Bookmarks extends SQLiteOpenHelper {

	private static String DBNAME = "LocationsDatabase.db";
	private static int VERSION = 1;
	public static final String FIELD_ROW_ID = "_id";
	public static final String FIELD_LAT = "lat";
	public static final String FIELD_LNG = "lng";
	public static final String ADDRESS = "address";
	public static final String LOCATION_NAME = "title";
	public static final String DATABASE_TABLE = "Bookmarks";

	private static String sql = "CREATE TABLE " + DATABASE_TABLE + " ("
			+ FIELD_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_LNG
			+ " INTEGER, " + FIELD_LAT + " INTEGER, " + LOCATION_NAME
			+ " TEXT, " + ADDRESS + " TEXT " + " ) ";

	public Bookmarks(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
