package fragments;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tools.TravellingModeSlidingMenuAdapter;
import trafficparser.ParseTask;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.DirectionsInputActivity;
import com.example.navigation.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;

import directions.DirectionsDownloadTask;
import directions.DirectionsMarkers;
import directions.DirectionsParserTask;

public class TravellingModeFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private SupportMapFragment fragment;

	private List<Marker> markers = new ArrayList<Marker>();

	public static ArrayList<LatLng> markerPoints;

	public static int travelling_mode;

	private DrawerLayout drawlayout = null;
	private ActionBarDrawerToggle actbardrawertoggle = null;
	private ListView listview = null;
	ToggleButton trafficButton;

	private String[] travellingModeNames;

	private int[] travellingModeIcons;

	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp
			.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	List<LatLng> latLngs;

	String bingUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	String bingKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	String url2;

	View root;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == DirectionsInputActivity.RESULT_CODE) {
			String from = data.getExtras().getString("from");
			String to = data.getExtras().getString("to");
			new DirectionsFetcher(from, to).execute();
		}
	}

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
		} else if (item.getItemId() == R.id.travelling_autocomplete_text_menu_item) {
			startActivityForResult(new Intent(getActivity(),
					DirectionsInputActivity.class),
					DirectionsInputActivity.RESULT_CODE);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		if (root != null) {

			ViewGroup parent = (ViewGroup) root.getParent();
			if (parent != null)

				parent.removeView(root);

		}

		try {

			root = inflater.inflate(R.layout.travelling_mode, container, false);

		} catch (InflateException e) {

			/* map is already there, just return view as it is */

		}

		FragmentManager fragmentManager = getFragmentManager();

		fragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map);

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.commit();

		googleMap = fragment.getMap();

		trafficButton = (ToggleButton) root.findViewById(R.id.traffic_button);

		if (googleMap != null) {
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.setMyLocationEnabled(true);
		}

		markerPoints = new ArrayList<LatLng>();

		travellingModeNames = new String[] { "Driving", "Walking", "Bicycling" };
		travellingModeIcons = new int[] { R.drawable.car, R.drawable.walk,
				R.drawable.bicycle };

		drawlayout = (DrawerLayout) root
				.findViewById(R.id.travelling_mode_layout);
		listview = (ListView) root
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

		trafficButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {

							LatLngBounds bounds = googleMap.getProjection()
									.getVisibleRegion().latLngBounds;
							if (getAreaInTheScreen(bounds) < 5000000) {
								new ParseTask(googleMap)
										.execute(urlBuilder(bounds));
							} else {
								googleMap.clear();
								if (markerPoints.size() != 0) {

									LatLng origin = markerPoints.get(0);
									LatLng dest = markerPoints.get(1);

									DirectionsMarkers.drawStartStopMarkers();

									// Getting URL to the Google Directions API
									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest, 1);

									DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

									// Start downloading json data from Google
									// Directions
									// API
									directionsdownloadTask.execute(url);

								} else {

								}

							}

						} else {
							googleMap.clear();

							if (markerPoints.size() != 0) {
								LatLng origin = markerPoints.get(0);
								LatLng dest = markerPoints.get(1);

								DirectionsMarkers.drawStartStopMarkers();

								// Getting URL to the Google Directions API
								String url = DirectionsParserTask
										.getDirectionsUrl(origin, dest, 1);

								DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

								// Start downloading json data from Google
								// Directions
								// API
								directionsdownloadTask.execute(url);
							}

						}
					}
				});

		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {

				if (trafficButton.getText().equals("Traffic On")) {
					LatLngBounds bounds = googleMap.getProjection()
							.getVisibleRegion().latLngBounds;
					if (getAreaInTheScreen(bounds) < 5000000) {
						new ParseTask(googleMap).execute(urlBuilder(bounds));
					} else {
						googleMap.clear();

						if (markerPoints.size() != 0) {
							LatLng origin = markerPoints.get(0);
							LatLng dest = markerPoints.get(1);

							DirectionsMarkers.drawStartStopMarkers();

							// Getting URL to the Google Directions API
							String url = DirectionsParserTask.getDirectionsUrl(
									origin, dest, 1);

							DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

							// Start downloading json data from Google
							// Directions
							// API
							directionsdownloadTask.execute(url);
						}
					}
				} else {

				}

			}

		});

		return root;

	}

	private String urlBuilder(LatLngBounds bounds) {
		double northLat = bounds.northeast.latitude;
		double northLong = bounds.northeast.longitude;
		double southLat = bounds.southwest.latitude;
		double southLong = bounds.southwest.longitude;

		url2 = bingUrl + String.valueOf(southLat) + ","
				+ String.valueOf(southLong) + "," + String.valueOf(northLat)
				+ "," + String.valueOf(northLong) + "?key=" + bingKey;
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

		float area = (Math.abs(width) / 1000) * (Math.abs(length) / 1000);

		return area;
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

	private class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {

		private String origin;
		private String destination;

		public DirectionsFetcher(String origin, String destination) {
			this.origin = origin;
			this.destination = destination;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			clearMarkers();
			getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);

		}

		protected Void doInBackground(URL... urls) {
			try {
				HttpRequestFactory requestFactory = HTTP_TRANSPORT
						.createRequestFactory(new HttpRequestInitializer() {
							@Override
							public void initialize(HttpRequest request) {
								request.setParser(new JsonObjectParser(
										JSON_FACTORY));
							}
						});

				GenericUrl url = new GenericUrl(
						"http://maps.googleapis.com/maps/api/directions/json");
				url.put("origin", origin);
				url.put("destination", destination);
				url.put("sensor", false);

				HttpRequest request = requestFactory.buildGetRequest(url);
				HttpResponse httpResponse = request.execute();
				DirectionsResult directionsResult = httpResponse
						.parseAs(DirectionsResult.class);

				String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
				latLngs = PolyUtil.decode(encodedPoints);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(Void result) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLngs.get(0), 7);
			googleMap.animateCamera(cameraUpdate);
			String url = DirectionsParserTask.getDirectionsUrl(latLngs.get(0),
					latLngs.get(latLngs.size() - 1), travelling_mode);

			DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

			directionsdownloadTask.execute(url);

		}
	}

	public static class DirectionsResult {

		@Key("routes")
		public List<Route> routes;

	}

	public static class Route {
		@Key("overview_polyline")
		public OverviewPolyLine overviewPolyLine;

	}

	public static class OverviewPolyLine {
		@Key("points")
		public String points;

	}

}