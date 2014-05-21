package com.example.view;

import com.example.model.Bookmarks;
import com.example.model.BookmarksContentProvider;
import com.example.model.BookmarksItem;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class EditDatabaseFragment extends DialogFragment {

	TextView addressTextView;
	TextView titleTextView;
	TextView addressEditText;
	EditText titleEditText;
	Button editButton;
	Button cancelButton;

	String addressText;
	String titleText;
	double latitude;
	double longitude;

	public static BookmarksItem bookmarksItem = null;
	DisplayMetrics metrics = null;
	long rowID;

	public EditDatabaseFragment() {
		super();
	}

	@SuppressWarnings("static-access")
	public EditDatabaseFragment(long id, BookmarksItem item, DisplayMetrics dm) {
		super();
		this.rowID = id;
		this.bookmarksItem = item;
		this.metrics = dm;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.edit_database_places_view, null);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		addressTextView = (TextView) view.findViewById(R.id.address_textview);
		titleTextView = (TextView) view.findViewById(R.id.title_textview);
		addressEditText = (TextView) view.findViewById(R.id.address_edittext);
		titleEditText = (EditText) view.findViewById(R.id.title_edittext);
		editButton = (Button) view.findViewById(R.id.edit_database_button);
		cancelButton = (Button) view.findViewById(R.id.cncl_database_button);

		addressEditText.setText(bookmarksItem.getBookmarksItemAddress());
		titleEditText.setText(bookmarksItem.getBookmarksItemTitle());

		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				updateLocationsToBookmarks();
				BookmarksFragment.bookmarksArrayAdapter.notifyDataSetChanged();
				getDialog().dismiss();

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getDialog().dismiss();
			}
		});

		return view;
	}

	public void updateLocationsToBookmarks() {

		ContentValues contentValues = new ContentValues();

		contentValues.put(Bookmarks.LOCATION_NAME, titleText);
		contentValues.put(Bookmarks.FIELD_LAT, latitude);
		contentValues.put(Bookmarks.FIELD_LNG, longitude);
		contentValues.put(Bookmarks.ADDRESS, addressText);

		final ContentResolver cr = TabActivity.mainContext.getContentResolver();
		cr.update(BookmarksContentProvider.CONTENT_URI, contentValues,
				Bookmarks.FIELD_ROW_ID + "=" + rowID, null);
		BookmarksFragment.bookmarksArrayAdapter.notifyDataSetChanged();

	}

}
