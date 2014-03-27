package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import places.Place;
import places.PlaceDialogFragment;
import places.PlacesParserTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FindNearbyPlacesFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();

	private static final float UNDEFINED_COLOR = -1;

	Spinner spinnerPlaces;

	public static Button buttonFind;

	Place[] nearPlaces;

	String[] nearPlacesType;

	String[] nearPlacesName;

	LatLng latLng;

	HashMap<String, Place> nearPlacesReference = new HashMap<String, Place>();

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
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		googleMap = getMap();

		View view = inflater.inflate(R.layout.nearby_places, container, false);

		buttonFind = (Button) view.findViewById(R.id.find_button_nearby_places);

		nearPlacesType = getResources().getStringArray(R.array.place_type);

		nearPlacesName = getResources().getStringArray(R.array.place_type_name);

		nearPlacesReference = new HashMap<String, Place>();

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity().getBaseContext(),
				android.R.layout.simple_spinner_dropdown_item, nearPlacesName);

		spinnerPlaces = (Spinner) view.findViewById(R.id.spr_place_type);

		spinnerPlaces.setAdapter(arrayAdapter);

		if (savedInstanceState != null) {

			// nearPlacesReference.clear();

			if (savedInstanceState.containsKey("places")) {

				nearPlaces = (Place[]) savedInstanceState
						.getParcelableArray("places");

				// Traversing through each near by place object
				for (int i = 0; i < nearPlaces.length; i++) {

					// Getting latitude and longitude of the i-th place
					LatLng point = new LatLng(
							Double.parseDouble(nearPlaces[i].placeLatitude),
							Double.parseDouble(nearPlaces[i].placeLongitude));

					// Drawing the marker corresponding to the i-th place
					Marker m = PlacesParserTask.drawMarker(point,
							UNDEFINED_COLOR);

					// Linkng i-th place and its marker id
					nearPlacesReference.put(m.getId(), nearPlaces[i]);
				}
			}

			// If a touched location is already saved
			if (savedInstanceState.containsKey("location")) {

				// Retrieving the touched location and setting in member
				// variable
				latLng = (LatLng) savedInstanceState.getParcelable("location");

				// Drawing a marker at the touched location
				PlacesParserTask.drawMarker(latLng,
						BitmapDescriptorFactory.HUE_GREEN);
			}
		}

		// Map Click listener
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				// Clears all the existing markers
				googleMap.clear();

				// Setting the touched location in member variable
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

				// Getting place object corresponding to the currently clicked
				// Marker
				Place place = nearPlacesReference.get(marker.getId());

				// Creating a dialog fragment to display the photo
				PlaceDialogFragment dialogFragment = new PlaceDialogFragment(
						place, TabActivity.displayMetrics);

				// Adding the dialog fragment to the transaction
				TabActivity.fragmentTransaction.add(dialogFragment, "TAG");

				// Committing the fragment transaction
				TabActivity.fragmentTransaction.commit();

				return false;
			}
		});

		buttonFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int selectedPosition = spinnerPlaces.getSelectedItemPosition();
				String type = nearPlacesType[selectedPosition];

				googleMap.clear();

				PlacesParserTask.drawMarker(latLng,
						BitmapDescriptorFactory.HUE_GREEN);

				StringBuilder sb = new StringBuilder(
						"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
				sb.append("location=" + latLng.latitude + ","
						+ latLng.longitude);
				sb.append("&radius=500000");
				sb.append("&types=" + type);
				sb.append("&sensor=true");
				sb.append("&key=AIzaSyC-CiTPvezMf-xewmsVJZp8P8PcnWkSJow");

				// Creating a new non-ui thread task to download Google place //
				// jsondata
				PlacesParserTask placesTask = new PlacesParserTask();

				// Invokes the "doInBackground()" method of the class PlaceTask
				placesTask.execute(sb.toString());
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		// Saving all the near by places objects
		if (nearPlaces != null)
			outState.putParcelableArray("places", nearPlaces);

		// Saving the touched location
		if (latLng != null)
			outState.putParcelable("location", latLng);

		super.onSaveInstanceState(outState);
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

	private Marker addMarker(LatLng latLng, float color) {
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
}
