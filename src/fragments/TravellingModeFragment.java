package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tools.TravellingModeSlidingMenuAdapter;
import trafficparser.ParseTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import directions.DirectionsDownloadTask;
import directions.DirectionsMarkers;
import directions.DirectionsParserTask;

import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TravellingModeFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private SupportMapFragment fragment;

	private List<Marker> markers = new ArrayList<Marker>();

	public static ArrayList<LatLng> markerPoints;

	public static int travelling_mode;

	private DrawerLayout drawlayout = null;
	private ActionBarDrawerToggle actbardrawertoggle = null;
	private ListView listview = null;

	private String[] travellingModeNames;

	private int[] travellingModeIcons;
	
	String bingUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	String bingKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	String url2;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static TravellingModeFragment newInstance(int position, String title) {
		TravellingModeFragment fragment = new TravellingModeFragment();
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
		inflater.inflate(R.menu.travelling_mode_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.travelling_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.travelling_toggle_style) {
			toggleStyle();
		}

		if (item.getItemId() == android.R.id.home) {
			if (drawlayout.isDrawerOpen(listview)) {
				drawlayout.closeDrawer(listview);
			} else {
				drawlayout.openDrawer(listview);
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onDestroyView() {

	    FragmentManager fm = getFragmentManager();

	    Fragment xmlFragment = fm.findFragmentById(R.id.map);
	    if (xmlFragment != null) {
	        fm.beginTransaction().remove(xmlFragment).commit();
	    }

	    super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		View view = inflater
				.inflate(R.layout.travelling_mode, container, false);
		FragmentManager fragmentManager = getFragmentManager();

		fragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map);

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.commit();

		googleMap = fragment.getMap();

		if (googleMap != null) {
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.setMyLocationEnabled(true);
		}

		markerPoints = new ArrayList<LatLng>();

		travellingModeNames = new String[] { "Driving", "Walking", "Bicycling" };
		travellingModeIcons = new int[] { R.drawable.car, R.drawable.walk,
				R.drawable.bicycle };

		drawlayout = (DrawerLayout) view
				.findViewById(R.id.travelling_mode_layout);
		listview = (ListView) view
				.findViewById(R.id.travelling_mode_sliding_menu);
		drawlayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawlayout.setBackgroundColor(Color.WHITE);

		TravellingModeSlidingMenuAdapter menuAdapter = new TravellingModeSlidingMenuAdapter(
				getSherlockActivity(), travellingModeNames, travellingModeIcons);

		listview.setAdapter(menuAdapter);

		actbardrawertoggle = new ActionBarDrawerToggle(getSherlockActivity(),
				drawlayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

		};
		drawlayout.setDrawerListener(actbardrawertoggle);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (position == 0) {

					travelling_mode = 1;

					if (markerPoints.size() >= 2) {
						LatLng origin = markerPoints.get(0);
						LatLng dest = markerPoints.get(1);

						// Getting URL to the Google Directions API
						String url = DirectionsParserTask.getDirectionsUrl(
								origin, dest, travelling_mode);

						DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

						// Start downloading json data from Google Directions
						// API
						directionsdownloadTask.execute(url);
						drawlayout.closeDrawer(listview);
					}
				} else if (position == 1) {

					travelling_mode = 2;

					if (markerPoints.size() >= 2) {
						LatLng origin = markerPoints.get(0);
						LatLng dest = markerPoints.get(1);

						// Getting URL to the Google Directions API
						String url = DirectionsParserTask.getDirectionsUrl(
								origin, dest, travelling_mode);

						DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

						// Start downloading json data from Google Directions
						// API
						directionsdownloadTask.execute(url);
						drawlayout.closeDrawer(listview);
					}

				} else if (position == 2) {

					travelling_mode = 3;

					if (markerPoints.size() >= 2) {
						LatLng origin = markerPoints.get(0);
						LatLng dest = markerPoints.get(1);

						// Getting URL to the Google Directions API
						String url = DirectionsParserTask.getDirectionsUrl(
								origin, dest, travelling_mode);

						DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

						// Start downloading json data from Google Directions
						// API
						directionsdownloadTask.execute(url);
						drawlayout.closeDrawer(listview);
					}

				}
			}
		});

		// Setting a click event handler for the map
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {

				// Already two locations
				if (markerPoints.size() > 1) {
					markerPoints.clear();
					googleMap.clear();
				}

				// Adding new item to the ArrayList
				markerPoints.add(arg0);

				// Draws Start and Stop markers on the Google Map
				DirectionsMarkers.drawStartStopMarkers();

			}
		});
		
		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				LatLngBounds bounds = googleMap.getProjection()
						.getVisibleRegion().latLngBounds;
				if (getAreaInTheScreen(bounds) < 5000000) {
					new ParseTask(googleMap).execute(urlBuilder(bounds));
				} else {
					googleMap.clear();
				}

			}

			private String urlBuilder(LatLngBounds bounds) {
				double northLat = bounds.northeast.latitude;
				double northLong = bounds.northeast.longitude;
				double southLat = bounds.southwest.latitude;
				double southLong = bounds.southwest.longitude;

				url2 = bingUrl + String.valueOf(southLat) + ","
						+ String.valueOf(southLong) + ","
						+ String.valueOf(northLat) + ","
						+ String.valueOf(northLong) + "?key=" + bingKey;
				return url2;

			}

			private float getAreaInTheScreen(LatLngBounds bounds) {
				double northLat = bounds.northeast.latitude;
				double northLong = bounds.northeast.longitude;
				double southLat = bounds.southwest.latitude;
				double southLong = bounds.southwest.longitude;

				Location l = new Location("southwest");
				l.setLatitude(southLat);
				l.setLongitude(southLong);
				Location l2 = new Location("southeast");
				l.setLatitude(southLat);
				l.setLongitude(northLong);
				float length = l.distanceTo(l2);

				Location l3 = new Location("northwest");
				l3.setLatitude(northLat);
				l3.setLongitude(southLong);
				float width = l.distanceTo(l3);

				float area = (Math.abs(width) / 1000)
						* (Math.abs(length) / 1000);

				return area;
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actbardrawertoggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actbardrawertoggle.onConfigurationChanged(newConfig);
	}

}
