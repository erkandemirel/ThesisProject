package fragments;

import com.example.navigation.R;
import com.example.navigation.TabActivity;

import database.Bookmarks;
import database.BookmarksContentProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddDatabaseFragment extends DialogFragment {

	TextView addressTextView;
	TextView titleTextView;
	EditText addressEditText;
	EditText titleEditText;
	Button addButton;
	Button cancelButton;

	String addressText;
	String titleText;
	double latitude;
	double longitude;

	public AddDatabaseFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.database_add_places_view, null);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		addressTextView = (TextView) view.findViewById(R.id.address_textview);
		titleTextView = (TextView) view.findViewById(R.id.title_textview);
		addressEditText = (EditText) view.findViewById(R.id.address_edittext);
		titleEditText = (EditText) view.findViewById(R.id.title_edittext);
		addButton = (Button) view.findViewById(R.id.add_database_button);
		cancelButton = (Button) view.findViewById(R.id.cancel_database_button);

		addressEditText.setText(PlaceDialogFragment.placeObject.vicinity);
		titleEditText.setText(PlaceDialogFragment.placeObject.placeName);

		addressText = addressEditText.getText().toString();
		titleText = titleEditText.getText().toString();

		try {

			latitude = Double
					.parseDouble(PlaceDialogFragment.placeObject.placeLatitude);
		} catch (NumberFormatException e) {

		}

		try {

			longitude = Double
					.parseDouble(PlaceDialogFragment.placeObject.placeLongitude);
		} catch (NumberFormatException e) {

		}

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addLocationsToBookmarks();
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

	public void addLocationsToBookmarks() {

		ContentValues contentValues = new ContentValues();

		contentValues.put(Bookmarks.LOCATION_NAME, titleText);
		contentValues.put(Bookmarks.FIELD_LAT, latitude);
		contentValues.put(Bookmarks.FIELD_LNG, longitude);
		contentValues.put(Bookmarks.ADDRESS, addressText);

		final ContentResolver cr = TabActivity.mainContext.getContentResolver();
		cr.insert(BookmarksContentProvider.CONTENT_URI, contentValues);
		BookmarksFragment.bookmarksArrayAdapter.notifyDataSetChanged();
	}

}
