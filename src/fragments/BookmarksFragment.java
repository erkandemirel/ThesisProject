package fragments;

import java.util.ArrayList;
import java.util.Random;

import tools.BookmarksArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import com.example.navigation.R;
import com.example.navigation.TabActivity;

import database.Bookmarks;
import database.BookmarksContentProvider;
import database.BookmarksItem;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressLint("NewApi")
public class BookmarksFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	ListView bookmarksListView;
	ArrayList<BookmarksItem> bookmarksItems;
	static BookmarksArrayAdapter bookmarksArrayAdapter;

	public long rowId;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static BookmarksFragment newInstance(int position, String title) {
		BookmarksFragment fragment = new BookmarksFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("title", title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		handler.postDelayed(runner, random.nextInt(2000));

		View view = inflater.inflate(R.layout.booksmark, container, false);

		bookmarksListView = (ListView) view
				.findViewById(R.id.bookmarkslistView);

		bookmarksItems = new ArrayList<BookmarksItem>();

		bookmarksArrayAdapter = new BookmarksArrayAdapter(
				TabActivity.mainContext, R.layout.bookmarks_item,
				bookmarksItems);
		bookmarksListView.setAdapter(bookmarksArrayAdapter);

		// getLoaderManager().initLoader(0, null, this);

		bookmarksListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {

				rowId = bookmarksItems.get(position).getBookmarksItemID();

				BookmarksItem item = (BookmarksItem) bookmarksListView
						.getItemAtPosition(position);

				DisplayMetrics dm = new DisplayMetrics();

				WindowManager windowManager = (WindowManager) TabActivity.mainContext
						.getSystemService(TabActivity.WINDOW_SERVICE);

				Display display = windowManager.getDefaultDisplay();

				display.getMetrics(dm);

				// Creating a dialog fragment to display the photo
				EditDatabaseFragment editDatabaseFragment = new EditDatabaseFragment(
						rowId, item, dm);

				FragmentManager fm = getFragmentManager();

				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add(editDatabaseFragment, "TAG");

				fragmentTransaction.commit();

			}

		});

		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("oldList", bookmarksItems);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(TabActivity.mainContext,
				BookmarksContentProvider.CONTENT_URI, null, null, null, null);
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

		while (cursor.moveToNext()) {

			final int id = cursor.getInt(idColumnIndex);
			final String title = cursor.getString(titleColumnIndex);
			final String address = cursor.getString(addressColumnIndex);

			bookmarksItems.add(new BookmarksItem(id, title, address));
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		bookmarksItems.clear();
		bookmarksArrayAdapter.notifyDataSetChanged();

	}

}
