package database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BookmarksContentProvider extends ContentProvider {

	public static final String AUTHORITY = "database.bookmarksprovider";

	public static final String BOOKMARKS_ITEM_PATH = "bookmarks";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/bookmarks");

	private static final UriMatcher mUriMatcher;
	private static final int BOOKMARKS_ITEM_PATH_TYPE = 1;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, BOOKMARKS_ITEM_PATH,
				BOOKMARKS_ITEM_PATH_TYPE);

	}

	private Bookmarks bookmarksDB;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		final String rowId = uri.getPathSegments().get(1);
		selection = Bookmarks.FIELD_ROW_ID
				+ "="
				+ rowId
				+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
						: "");

		int deleteCount = db.delete(Bookmarks.DATABASE_TABLE, selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		final long rowID = db.insert(Bookmarks.DATABASE_TABLE, null,
				contentValues);

		getContext().getContentResolver().notifyChange(CONTENT_URI, null);

		return ContentUris.withAppendedId(CONTENT_URI, rowID);
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

		String rowId = uri.getPathSegments().get(1);
		queryBuilder.appendWhere(Bookmarks.FIELD_ROW_ID + "=" + rowId);

		final Cursor cursor = queryBuilder.query(db, columns, selection,
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = bookmarksDB.getWritableDatabase();

		String rowId = uri.getPathSegments().get(1);
		selection = Bookmarks.FIELD_ROW_ID
				+ "="
				+ rowId
				+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
						: "");

		final int updateCount = db.update(Bookmarks.DATABASE_TABLE,
				contentValues, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return updateCount;
	}

}
