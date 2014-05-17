package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import places.Place;
import places.PlacesDownloadTask;
import tools.NearbyPlacesSlidingMenuAdapter;

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

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import autocompletetext.AutoCompletePlaceDownloadTask;
import autocompletetext.AutoCompletePlaceParserTask;

public class FindNearbyPlacesFragment extends SherlockMapFragment {

	public static GoogleMap nearbyPlacesGoogleMap;

	private SupportMapFragment nearbyPlacesfragment;

	private ArrayList<Marker> nearbyPlacesMarkerList;

	public static final int PLACES = 0;

	public static final int PLACES_DETAILS = 1;

	Place[] nearbyPlaces;
	private String[] nearbyPlaceNames;
	private int[] nearbyPlaceIcons;

	public static LatLng nearbyPlaceslatLng;

	public static AutoCompleteTextView nearbyPlacesAutoCompleteTextView;

	public static HashMap<String, Place> nearPlacesReference;

	AutoCompletePlaceDownloadTask placesDownloadTask;
	AutoCompletePlaceDownloadTask placeDetailsDownloadTask;
	AutoCompletePlaceParserTask placesParserTask;
	AutoCompletePlaceParserTask placeDetailsParserTask;

	private DrawerLayout nearbyPlacesDrawerLayout = null;
	private ListView nearbyPlaceslistview = null;
	private ActionBarDrawerToggle nearbyPlacesDrawerToggle = null;

	private View nearbyPlacesRootView;
	
	public static String placeType;

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

		switch (item.getItemId()) {
		case R.id.places_normal_map:
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;

		case R.id.places_satellite_map:
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;

		case R.id.places_terrain_map:
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;

		case R.id.places_hybrid_map:
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;

		case R.id.places_clear_locations:
			clearMarkers();
			break;
		case android.R.id.home:

			if (nearbyPlacesDrawerLayout.isDrawerOpen(nearbyPlaceslistview)) {
				nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
			} else {
				nearbyPlacesDrawerLayout.openDrawer(nearbyPlaceslistview);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		if (nearbyPlacesRootView != null) {
			ViewGroup parent = (ViewGroup) nearbyPlacesRootView.getParent();
			if (parent != null)
				parent.removeView(nearbyPlacesRootView);
		}
		try {
			nearbyPlacesRootView = inflater.inflate(R.layout.nearby_places,
					container, false);
		} catch (InflateException e) {

		}

		FragmentManager fragmentManager = getFragmentManager();

		nearbyPlacesfragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.nearby_places_map);

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.commit();

		nearbyPlacesGoogleMap = nearbyPlacesfragment.getMap();

		if (nearbyPlacesGoogleMap != null) {
			nearbyPlacesGoogleMap.getUiSettings().setCompassEnabled(true);
			nearbyPlacesGoogleMap.setMyLocationEnabled(true);
		}

		nearbyPlacesMarkerList = new ArrayList<Marker>();

		nearPlacesReference = new HashMap<String, Place>();

		nearbyPlaceNames = new String[] { "Airport", "Bank", "Bus Station",
				"Hospital", "Mosque", "Restaurant" };
		nearbyPlaceIcons = new int[] { R.drawable.airport, R.drawable.bank,
				R.drawable.bus, R.drawable.hospital, R.drawable.mosque,
				R.drawable.restaurant };
		nearbyPlacesDrawerLayout = (DrawerLayout) nearbyPlacesRootView
				.findViewById(R.id.drawer_layout);

		nearbyPlaceslistview = (ListView) nearbyPlacesRootView
				.findViewById(R.id.left_drawer);

		nearbyPlacesDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		nearbyPlacesDrawerLayout.setBackgroundColor(Color.WHITE);
		NearbyPlacesSlidingMenuAdapter menuAdapter = new NearbyPlacesSlidingMenuAdapter(
				getSherlockActivity(), nearbyPlaceNames, nearbyPlaceIcons);
		nearbyPlaceslistview.setAdapter(menuAdapter);

		nearbyPlacesDrawerToggle = new ActionBarDrawerToggle(
				getSherlockActivity(), nearbyPlacesDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

		};
		nearbyPlacesDrawerLayout.setDrawerListener(nearbyPlacesDrawerToggle);

		nearbyPlaceslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (position == 0) {
					placeType="airport";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				} else if (position == 1) {
					placeType="bank";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				} else if (position == 2) {
					placeType="bus_station";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				} else if (position == 3) {
					placeType="hospital";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				} else if (position == 4) {
					placeType="mosque";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				} else if (position == 5) {
					placeType="restaurant";
					getNearbyPlaces(placeType);
					nearbyPlacesDrawerLayout.closeDrawer(nearbyPlaceslistview);
				}

			}
		});

		// Handling screen rotation
		/*
		 * if (savedInstanceState != null) {
		 * 
		 * // Removes all the existing links from marker id to place object
		 * nearPlacesReference.clear();
		 * 
		 * // If near by places are already saved if
		 * (savedInstanceState.containsKey("places")) {
		 * 
		 * // Retrieving the array of place objects nearPlaces = (Place[])
		 * savedInstanceState .getParcelableArray("places");
		 * 
		 * // Traversing through each near by place object for (int i = 0; i <
		 * nearPlaces.length; i++) {
		 * 
		 * // Getting latitude and longitude of the i-th place LatLng point =
		 * new LatLng( Double.parseDouble(nearPlaces[i].placeLatitude),
		 * Double.parseDouble(nearPlaces[i].placeLongitude));
		 * 
		 * // Drawing the marker corresponding to the i-th place Marker m =
		 * addMarker(point, UNDEFINED_COLOR);
		 * 
		 * // Linkng i-th place and its marker id
		 * nearPlacesReference.put(m.getId(), nearPlaces[i]); } }
		 * 
		 * // If a touched location is already saved if
		 * (savedInstanceState.containsKey("location")) {
		 * 
		 * // Retrieving the touched location and setting in member // variable
		 * latLng = (LatLng) savedInstanceState.getParcelable("location");
		 * 
		 * // Drawing a marker at the touched location addMarker(latLng,
		 * BitmapDescriptorFactory.HUE_GREEN); } }
		 */

		nearbyPlacesGoogleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				nearbyPlaceslatLng = point;
				addMarker(nearbyPlaceslatLng);
			}
		});

		nearbyPlacesGoogleMap
				.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {

						if (!nearPlacesReference.containsKey(marker.getId()))
							return false;

						Place place = nearPlacesReference.get(marker.getId());

						DisplayMetrics dm = new DisplayMetrics();

						WindowManager windowManager = (WindowManager) TabActivity.mainContext
								.getSystemService(TabActivity.WINDOW_SERVICE);

						Display display = windowManager.getDefaultDisplay();

						display.getMetrics(dm);

						PlaceDialogFragment dialogFragment = new PlaceDialogFragment(
								place, dm);

						FragmentManager fm = getFragmentManager();

						FragmentTransaction fragmentTransaction = fm
								.beginTransaction();

						fragmentTransaction.add(dialogFragment, "TAG");

						fragmentTransaction.commit();

						return false;
					}
				});

		return nearbyPlacesRootView;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		if (nearbyPlaces != null)
			outState.putParcelableArray("places", nearbyPlaces);

		if (nearbyPlaceslatLng != null)
			outState.putParcelable("location", nearbyPlaceslatLng);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		nearbyPlacesDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		nearbyPlacesDrawerToggle.onConfigurationChanged(newConfig);
	}

	// ***** External Methods *****

	public void toggleStyle() {
		if (GoogleMap.MAP_TYPE_NORMAL == nearbyPlacesGoogleMap.getMapType()) {
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			nearbyPlacesGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	public void clearMarkers() {
		nearbyPlacesGoogleMap.clear();
		nearbyPlacesMarkerList.clear();
	}

	public static Marker addMarker(LatLng latLng) {

		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.position(latLng);

		markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.marker_icon));

		Marker m = nearbyPlacesGoogleMap.addMarker(markerOptions);

		return m;
	}

	private void getNearbyPlaces(String type) {

		nearbyPlacesGoogleMap.clear();

		if (nearbyPlaceslatLng == null) {
			Toast.makeText(getSherlockActivity(), "No points on the map!",
					Toast.LENGTH_LONG).show();
		} else {

			StringBuilder sb = new StringBuilder(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			sb.append("location=" + nearbyPlaceslatLng.latitude + ","
					+ nearbyPlaceslatLng.longitude);
			sb.append("&radius=500000");
			sb.append("&types=" + type);
			sb.append("&sensor=true");
			sb.append("&key=AIzaSyC-CiTPvezMf-xewmsVJZp8P8PcnWkSJow");

			PlacesDownloadTask placesDownloadTask = new PlacesDownloadTask();

			placesDownloadTask.execute(sb.toString());

		}
	}

}
