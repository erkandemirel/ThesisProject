package fragments;

import java.util.ArrayList;
import java.util.Random;

import tools.BookmarksArrayAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.navigation.R;
import com.example.navigation.TabActivity;

import database.Bookmarks;
import database.BookmarksContentProvider;
import database.BookmarksItem;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

@SuppressLint("NewApi")
public class BookmarksFragment extends SherlockFragment implements
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
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(0, null, this);
	}

	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		handler.postDelayed(runner, random.nextInt(2000));

		View view = inflater.inflate(R.layout.booksmark, container, false);

		bookmarksListView = (ListView) view
				.findViewById(R.id.bookmarksListView);

		bookmarksItems = new ArrayList<BookmarksItem>();

		bookmarksArrayAdapter = new BookmarksArrayAdapter(getActivity(),
				R.layout.bookmarks_item, bookmarksItems);
		bookmarksListView.setAdapter(bookmarksArrayAdapter);

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
						MenuInflater inflater = getActivity().getMenuInflater();
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

		return view;

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(),
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

			System.out.println(id + address + title);

			bookmarksItems.add(new BookmarksItem(id, title, address));
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