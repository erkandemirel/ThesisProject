package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import places.Place;
import places.PlaceDialogFragment;
import places.PlacesDownloadTask;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import autocompletetext.AutoCompletePlaceDownloadTask;
import autocompletetext.AutoCompletePlaceParserTask;
import autocompletetext.AutoCompletePlaceUrl;

public class FindNearbyPlacesFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();

	private static final float UNDEFINED_COLOR = -1;

	public static final int PLACES = 0;

	public static final int PLACES_DETAILS = 1;

	Place[] nearPlaces;

	String[] nearPlacesType;

	String[] nearPlacesName;

	public static LatLng latLng;

	public static AutoCompleteTextView textViewPlaces;

	Button nearbyPlacesButton;

	HashMap<String, Place> nearPlacesReference = new HashMap<String, Place>();

	AutoCompletePlaceDownloadTask placesDownloadTask;
	AutoCompletePlaceDownloadTask placeDetailsDownloadTask;
	AutoCompletePlaceParserTask placesParserTask;
	AutoCompletePlaceParserTask placeDetailsParserTask;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static FindNearbyPlacesFragment newInstance(int position,
			String title) {
		FindNearbyPlacesFragment fragment = new FindNearbyPlacesFragment();
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
		inflater.inflate(R.menu.nearby_places_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		} else if (item.getItemId() == R.id.airport) {
			getNearbyPlaces("airport");
		} else if (item.getItemId() == R.id.restaurant) {
			getNearbyPlaces("restaurant");
		} else if (item.getItemId() == R.id.mosque) {
			getNearbyPlaces("mosque");
		} else if (item.getItemId() == R.id.movie_theater) {
			getNearbyPlaces("movie_theater");
		} else if (item.getItemId() == R.id.hospital) {
			getNearbyPlaces("hospital");
		} else if (item.getItemId() == R.id.bank) {
			getNearbyPlaces("bank");
		} else if (item.getItemId() == R.id.atm) {
			getNearbyPlaces("atm");
		} else if (item.getItemId() == R.id.cinema) {
			getNearbyPlaces("cinema");
		} else if (item.getItemId() == R.id.bus_station) {
			getNearbyPlaces("bus_station");
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		final View view = inflater.inflate(R.layout.nearby_places, container,
				false);

		nearbyPlacesButton = (Button) view
				.findViewById(R.id.nearby_places_button);

		textViewPlaces = (AutoCompleteTextView) view
				.findViewById(R.id.actv_places);
		textViewPlaces.setThreshold(1);

		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.commit();

		SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.nearby_places_map);

		googleMap = supportMapFragment.getMap();

		nearPlacesReference = new HashMap<String, Place>();

		// Map Click listener
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				latLng = point;

				// Drawing a marker at the touched location
				addMarker(latLng, BitmapDescriptorFactory.HUE_GREEN);
			}
		});

		// Marker click listener
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				// If touched at User input location
				if (!nearPlacesReference.containsKey(marker.getId()))
					return false;

				Place place = nearPlacesReference.get(marker.getId());

				DisplayMetrics dm = new DisplayMetrics();

				WindowManager windowManager = (WindowManager) TabActivity.mainContext
						.getSystemService(TabActivity.WINDOW_SERVICE);

				Display display = windowManager.getDefaultDisplay();

				display.getMetrics(dm);

				// Creating a dialog fragment to display the photo
				PlaceDialogFragment dialogFragment = new PlaceDialogFragment(
						place, dm);

				FragmentManager fm = getFragmentManager();

				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add(dialogFragment, "TAG");

				fragmentTransaction.commit();

				return false;
			}
		});

		textViewPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"
				placesDownloadTask = new AutoCompletePlaceDownloadTask(PLACES);

				// Getting url to the Google Places Autocomplete api
				String url = AutoCompletePlaceUrl.getAutoCompleteUrl(s
						.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		textViewPlaces.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long id) {

				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

				HashMap<String, String> hm = (HashMap<String, String>) adapter
						.getItem(index);

				// Creating a DownloadTask to download Places details of
				// the
				// selected place
				placeDetailsDownloadTask = new AutoCompletePlaceDownloadTask(
						PLACES_DETAILS);

				// Getting url to the Google Places details api
				String url = AutoCompletePlaceUrl.getPlaceDetailsUrl(hm
						.get("reference"));

				// Start downloading Google Place Details
				// This causes to execute doInBackground() of
				// DownloadTask class
				placeDetailsDownloadTask.execute(url);

			}
		});

		return view;

	}

	public void toggleStyle() {
		if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	public void clearMarkers() {
		googleMap.clear();
		markers.clear();
	}

	public static Marker addMarker(LatLng latLng, float color) {
		// Creating a marker
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting the position for the marker
		markerOptions.position(latLng);

		if (color != UNDEFINED_COLOR)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));

		// Placing a marker on the touched position
		Marker m = googleMap.addMarker(markerOptions);

		return m;
	}

	private void getNearbyPlaces(String type) {

		googleMap.clear();

		if (latLng == null) {
			Toast.makeText(getSherlockActivity(), "No points on the map!",
					Toast.LENGTH_LONG).show();
		} else {
			// PlacesParserTask.drawMarker(latLng,
			// BitmapDescriptorFactory.HUE_GREEN);

			StringBuilder sb = new StringBuilder(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			sb.append("location=" + latLng.latitude + "," + latLng.longitude);
			sb.append("&radius=500000");
			sb.append("&types=" + type);
			sb.append("&sensor=true");
			sb.append("&key=AIzaSyC-CiTPvezMf-xewmsVJZp8P8PcnWkSJow");

			// Creating a new non-ui thread task to download Google place //
			// jsondata
			PlacesDownloadTask placesDownloadTask = new PlacesDownloadTask();

			// Invokes the "doInBackground()" method of the class PlaceTask
			placesDownloadTask.execute(sb.toString());

		}
	}

}
