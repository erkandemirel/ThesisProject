package com.example.view;

import java.util.ArrayList;

import com.example.controller.BookmarksArrayAdapter;
import com.example.model.Bookmarks;
import com.example.model.BookmarksContentProvider;
import com.example.model.BookmarksItem;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

@SuppressLint("NewApi")
public class BookmarksFragment extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public static final int RESULT_CODE = 1234;
	public static ListView bookmarksListView;
	ArrayList<BookmarksItem> bookmarksItems;
	static BookmarksArrayAdapter bookmarksArrayAdapter;

	public long rowId;

	public static Activity bookmarksActivity;

	@SuppressWarnings({ "static-access" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.booksmark);

		bookmarksListView = (ListView) findViewById(R.id.bookmarksListView);

		bookmarksItems = BookmarksContentProvider.getAllBookmarksItem();
		bookmarksArrayAdapter = new BookmarksArrayAdapter(this,
				R.layout.bookmarks_item, bookmarksItems);
		bookmarksListView.setAdapter(bookmarksArrayAdapter);
		bookmarksArrayAdapter.notifyDataSetChanged();

		bookmarksActivity =this;

		getSupportLoaderManager().initLoader(0, null, this);

		bookmarksListView
				.setChoiceMode(bookmarksListView.CHOICE_MODE_MULTIPLE_MODAL);

		bookmarksListView
				.setMultiChoiceModeListener(new MultiChoiceModeListener() {

					@Override
					public boolean onPrepareActionMode(ActionMode arg0,
							Menu arg1) {

						return false;
					}

					@Override
					public void onDestroyActionMode(ActionMode arg0) {
						bookmarksArrayAdapter.removeSelection();

					}

					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						MenuInflater inflater = getMenuInflater();
						inflater.inflate(R.menu.bookmarks_context_menu, menu);
						return true;
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode,
							MenuItem item) {
						switch (item.getItemId()) {
						case R.id.delete:
							SparseBooleanArray selected = bookmarksArrayAdapter
									.getSelectedIds();
							for (int i = (selected.size() - 1); i >= 0; i--) {
								if (selected.valueAt(i)) {
									BookmarksItem selecteditem = bookmarksArrayAdapter
											.getItem(selected.keyAt(i));
									bookmarksArrayAdapter.remove(selecteditem);
									int a = selecteditem.getBookmarksItemID();
									delete_byID(a);
									a = -1;
								}
							}
							mode.finish();
						}
						return false;
					}

					@Override
					public void onItemCheckedStateChanged(ActionMode mode,
							int position, long id, boolean checked) {
						final int checkedCount = bookmarksListView
								.getCheckedItemCount();
						mode.setTitle(checkedCount + " Selected");
						bookmarksArrayAdapter.toggleSelection(position);
					}
				});

		bookmarksListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {

						bookmarksListView.setItemChecked(position, true);
						return false;
					}

				});

		bookmarksListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				rowId = bookmarksItems.get(position).getBookmarksItemID();

				BookmarksItem item = (BookmarksItem) bookmarksListView
						.getItemAtPosition(position);

				DisplayMetrics dm = new DisplayMetrics();

				WindowManager windowManager = (WindowManager) TabActivity.mainContext
						.getSystemService(TabActivity.WINDOW_SERVICE);

				Display display = windowManager.getDefaultDisplay();

				display.getMetrics(dm);

				EditDatabaseFragment editDatabaseFragment = new EditDatabaseFragment(
						rowId, item, dm);

				FragmentManager fm = getSupportFragmentManager();

				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add(editDatabaseFragment, "TAG");

				fragmentTransaction.commit();

			}

		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("oldList", bookmarksItems);
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this, BookmarksContentProvider.CONTENT_URI,
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		bookmarksItems.clear();

		final int idColumnIndex = cursor
				.getColumnIndexOrThrow(Bookmarks.FIELD_ROW_ID);

		final int titleColumnIndex = cursor
				.getColumnIndexOrThrow(Bookmarks.LOCATION_NAME);

		final int addressColumnIndex = cursor
				.getColumnIndexOrThrow(Bookmarks.ADDRESS);
		
		final int latitudeColumnIndex = cursor
				.getColumnIndexOrThrow(Bookmarks.FIELD_LAT);
		
		final int longitudeColumnIndex = cursor
				.getColumnIndexOrThrow(Bookmarks.FIELD_LNG);

		while (cursor.moveToNext()) {

			final int id = cursor.getInt(idColumnIndex);
			final String title = cursor.getString(titleColumnIndex);
			final String address = cursor.getString(addressColumnIndex);
			final double latitude = cursor.getDouble(latitudeColumnIndex);
			final double longitude = cursor.getDouble(longitudeColumnIndex);
			bookmarksItems.add(new BookmarksItem(id, title, address,latitude,longitude));
		}
		bookmarksArrayAdapter.notifyDataSetChanged();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		bookmarksItems.clear();
		bookmarksArrayAdapter.notifyDataSetChanged();

	}

	public void delete_byID(long id) {
		final ContentResolver cr = TabActivity.mainContext.getContentResolver();

		cr.delete(BookmarksContentProvider.CONTENT_URI, Bookmarks.FIELD_ROW_ID
				+ "=" + id, null);
	}

}
