package database;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BookmarksContentProvider extends ContentProvider {

	public static final String AUTHORITY = "database.bookmarksprovider";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

	public static final String BOOKMARKS_ITEM_PATH = "bookmarks";
	public static final String BOOKMARKS_ITEM_PATH_FOR_ID = BOOKMARKS_ITEM_PATH
			+ "/#";

	public static final Uri CONTENT_URI = BASE_URI.buildUpon()
			.appendPath(BOOKMARKS_ITEM_PATH).build();

	private static final UriMatcher mUriMatcher;
	private static final int BOOKMARKS_ITEM_PATH_TYPE = 1;
	private static final int BOOKMARKS_ITEM_PATH_TYPE_FOR_ID = 2;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, BOOKMARKS_ITEM_PATH,
				BOOKMARKS_ITEM_PATH_TYPE);
		mUriMatcher.addURI(AUTHORITY, BOOKMARKS_ITEM_PATH_FOR_ID,
				BOOKMARKS_ITEM_PATH_TYPE_FOR_ID);

	}

	private static Bookmarks bookmarksDB;

	private static SQLiteDatabase database;

	private static String[] allColumns = { Bookmarks.FIELD_ROW_ID,
			Bookmarks.FIELD_LNG, Bookmarks.FIELD_LAT, Bookmarks.LOCATION_NAME,
			Bookmarks.ADDRESS };

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		switch (mUriMatcher.match(uri)) {

		case BOOKMARKS_ITEM_PATH_TYPE_FOR_ID: {
			final String rowId = uri.getPathSegments().get(1);
			selection = Bookmarks.FIELD_ROW_ID
					+ "="
					+ rowId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			break;
		}
		default: {
			break;
		}
		}

		if (selection == null) {
			selection = "1";
		}

		int deleteCount = db.delete(Bookmarks.DATABASE_TABLE, selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return deleteCount;

	}

	@Override
	public String getType(Uri uri) {

		final String subType = "/vnd.database.bookmarks";
		switch (mUriMatcher.match(uri)) {
		case BOOKMARKS_ITEM_PATH_TYPE: {
			return ContentResolver.CURSOR_DIR_BASE_TYPE + subType;
		}
		case BOOKMARKS_ITEM_PATH_TYPE_FOR_ID: {
			return ContentResolver.CURSOR_ITEM_BASE_TYPE + subType;
		}
		default: {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		switch (mUriMatcher.match(uri)) {
		case BOOKMARKS_ITEM_PATH_TYPE: {

			final long rowID = db.insert(Bookmarks.DATABASE_TABLE, null,
					contentValues);

			getContext().getContentResolver().notifyChange(CONTENT_URI, null);

			return ContentUris.withAppendedId(CONTENT_URI, rowID);

		}
		default: {
			throw new IllegalArgumentException("URI: " + uri
					+ " is not supported");
		}
		}
	}

	@Override
	public boolean onCreate() {
		bookmarksDB = new Bookmarks(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {

		final SQLiteDatabase db = bookmarksDB.getReadableDatabase();

		final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		queryBuilder.setTables(Bookmarks.DATABASE_TABLE);

		switch (mUriMatcher.match(uri)) {
		case BOOKMARKS_ITEM_PATH_TYPE_FOR_ID: {
			String rowId = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(Bookmarks.FIELD_ROW_ID + "=" + rowId);
			break;
		}
		default: {
			break;
		}
		}

		final Cursor cursor = queryBuilder.query(db, columns, selection,
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		switch (mUriMatcher.match(uri)) {
		case BOOKMARKS_ITEM_PATH_TYPE_FOR_ID: {

			String rowId = uri.getPathSegments().get(1);
			selection = Bookmarks.FIELD_ROW_ID
					+ "="
					+ rowId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			break;
		}
		default: {
			break;
		}
		}

		final int updateCount = db.update(Bookmarks.DATABASE_TABLE,
				contentValues, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return updateCount;
	}

	public static void open() throws SQLException {
		database = bookmarksDB.getWritableDatabase();
	}

	public static void close() {
		bookmarksDB.close();
	}

	public static ArrayList<BookmarksItem> getAllBookmarksItem() {
		ArrayList<BookmarksItem> list = new ArrayList<BookmarksItem>();
		database = bookmarksDB.getWritableDatabase();
		Cursor cursor = database.query(Bookmarks.DATABASE_TABLE, allColumns,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BookmarksItem item = cursorToComment(cursor);
			list.add(item);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return list;

	}

	private static BookmarksItem cursorToComment(Cursor cursor) {
		BookmarksItem item = new BookmarksItem();
		item.setBookmarksItemID(cursor.getInt(0));
		item.setBookmarksItemLongitude(cursor.getDouble(1));
		item.setBookmarksItemLatitude(cursor.getDouble(2));
		item.setBookmarksItemTitle(cursor.getString(3));
		item.setBookmarksItemAddress(cursor.getString(4));
		return item;
	}

}
