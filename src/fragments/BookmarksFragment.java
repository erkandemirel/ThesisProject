package fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import database.Bookmarks;
import database.BookmarksContentProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookmarksFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();

	public LatLng latLng;

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.booksmark_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		} else if (item.getItemId() == R.id.action_bar_add_location_to_database) {

			addLocationsToBookmarks(latLng);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		View view = inflater.inflate(R.layout.booksmark, container, false);
		FragmentManager fragmentManager = getFragmentManager();

		SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.booksmark_map);
		googleMap = supportMapFragment.getMap();

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.commit();

		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				googleMap.clear();

				latLng = point;

				addMarker(latLng);
			}
		});

		return view;
	}

	public void addLocationsToBookmarks(LatLng latLng) {

		Geocoder geocoder = new Geocoder(TabActivity.mainContext);
		ContentValues contentValues = new ContentValues();

		double latitude = latLng.latitude;
		double longitude = latLng.longitude;

		List<Address> addresses = null;
		String addressText = "";

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);

			addressText = String.format("%s, %s, %s", address
					.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0)
					: "", address.getLocality(), address.getCountryName());
		}
		String title = "Home";

		contentValues.put(Bookmarks.LOCATION_NAME, title);
		contentValues.put(Bookmarks.FIELD_LAT, latitude);
		contentValues.put(Bookmarks.FIELD_LNG, longitude);
		contentValues.put(Bookmarks.ADDRESS, addressText);
		
		final ContentResolver cr = TabActivity.mainContext.getContentResolver();
		cr.insert(BookmarksContentProvider.CONTENT_URI, contentValues);

		//LocationInsertTask insertTask = new LocationInsertTask();

		// Storing the latitude, longitude and zoom level to SQLite database
		//insertTask.execute(contentValues);

	}

	public void toggleStyle() {
		if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	public static Marker addMarker(LatLng latLng) {
		// Creating a marker
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting the position for the marker
		markerOptions.position(latLng);

		// Placing a marker on the touched position
		Marker m = googleMap.addMarker(markerOptions);

		return m;
	}

	public void clearMarkers() {
		googleMap.clear();
		markers.clear();
	}

	private class LocationInsertTask extends
			AsyncTask<ContentValues, Void, Void> {
		@Override
		protected Void doInBackground(ContentValues... contentValues) {

			/**
			 * Setting up values to insert the clicked location into SQLite
			 * database
			 */
			TabActivity.mainContext.getContentResolver().insert(
					BookmarksContentProvider.CONTENT_URI, contentValues[0]);
			return null;
		}
	}

}
