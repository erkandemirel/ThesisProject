package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Bookmarks extends SQLiteOpenHelper {

	private static String DBNAME = "UsersLocationsDatabase";
	private static int VERSION = 1;
	public static final String FIELD_ROW_ID = "_id";
	public static final String FIELD_LAT = "lat";
	public static final String FIELD_LNG = "lng";
	public static final String ADDRESS = "address";
	public static final String LOCATION_NAME = "title";
	public static final String DATABASE_TABLE = "Bookmarks";

	private static String sql = "create table " + DATABASE_TABLE + " ( "
			+ FIELD_ROW_ID + " integer primary key autoincrement , "
			+ FIELD_LNG + " double , " + FIELD_LAT + " double , "
			+ LOCATION_NAME + "text not null," + ADDRESS + "text not null"
			+ " ) ";

	public Bookmarks(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);

		onCreate(db);
	}

}
