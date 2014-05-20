package fragments;

import java.util.ArrayList;
import java.util.Random;

import tools.GPSTracker;
import tools.TravellingModeSlidingMenuAdapter;
import trafficparser.ParseTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.AutoCompleteDirectionsActivity;
import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import directions.DirectionsDownloadTask;
import directions.DirectionsFetcher;
import directions.DirectionsMarkers;
import directions.DirectionsParserTask;
import directions.DirectionsReverseGeocodingTask;

public class TravellingModeFragment extends SherlockMapFragment {

	public static GoogleMap travellingModeGoogleMap;

	private SupportMapFragment travellingModeFragment;

	public static ArrayList<LatLng> travellingModeMarkerLocations;

	public static int travelling_mode;

	private DrawerLayout travellingModeDrawerLayout = null;

	private ActionBarDrawerToggle travellingModeDrawerToggle = null;

	private ListView travellingModeListview = null;

	public static TextView distanceDurationView;

	private View travellingModeRootView;

	private ToggleButton trafficAccidentButton;

	private String[] travellingModeNames;

	private int[] travellingModeIcons;

	public static int checkedView = 0;

	public static Activity travellingModeActivity;

	private String bingServerAccidentUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	private String bingAPIKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	private String bingServerCompleteUrl;

	DirectionsDownloadTask directionsdownloadTask;

	public static ProgressDialog progressDialog;
	GPSTracker gps = new GPSTracker(TabActivity.mainContext);
	DirectionsReverseGeocodingTask reverseGeocodingTask = new DirectionsReverseGeocodingTask(
			gps);

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static TravellingModeFragment newInstance(int position, String title) {
		TravellingModeFragment travellingModeFragment = new TravellingModeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("title", title);
		travellingModeFragment.setArguments(bundle);
		return travellingModeFragment;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.travelling_mode_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.travelling_normal_map:
			travellingModeGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;

		case R.id.travelling_satellite_map:
			travellingModeGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;

		case R.id.travelling_terrain_map:
			travellingModeGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;

		case R.id.travelling_hybrid_map:
			travellingModeGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;

		case R.id.shw_traffic:
			if (travellingModeGoogleMap.isTrafficEnabled()) {
				travellingModeGoogleMap.setTrafficEnabled(false);
			} else {
				travellingModeGoogleMap.setTrafficEnabled(true);
			}
			break;

		case R.id.travelling_clear_locations:
			clearMarkers();
			break;
		case R.id.get_route:
			startActivityForResult(new Intent(getActivity(),
					AutoCompleteDirectionsActivity.class),
					AutoCompleteDirectionsActivity.RESULT_CODE);
			break;
		case R.id.bookmarks:
			startActivityForResult(new Intent(getActivity(),
					BookmarksFragment.class), BookmarksFragment.RESULT_CODE);
			break;
		case android.R.id.home:
			if (travellingModeDrawerLayout.isDrawerOpen(travellingModeListview)) {
				travellingModeDrawerLayout.closeDrawer(travellingModeListview);
			} else {
				travellingModeDrawerLayout.openDrawer(travellingModeListview);
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

		if (travellingModeRootView != null) {

			ViewGroup parent = (ViewGroup) travellingModeRootView.getParent();
			if (parent != null)

				parent.removeView(travellingModeRootView);

		}

		try {

			travellingModeRootView = inflater.inflate(R.layout.travelling_mode,
					container, false);

		} catch (InflateException e) {

		}

		FragmentManager fragmentManager = getFragmentManager();

		travellingModeFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map);

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.commit();

		travellingModeGoogleMap = travellingModeFragment.getMap();

		trafficAccidentButton = (ToggleButton) travellingModeRootView
				.findViewById(R.id.traffic_button);
		trafficAccidentButton.getBackground().setAlpha(128);

		if (travellingModeGoogleMap != null) {
			travellingModeGoogleMap.getUiSettings().setCompassEnabled(true);
			travellingModeGoogleMap.setMyLocationEnabled(true);
		}

		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(gps
				.getLatitude(), gps.getLongitude()));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

		travellingModeGoogleMap.moveCamera(center);
		travellingModeGoogleMap.animateCamera(zoom);

		travellingModeMarkerLocations = new ArrayList<LatLng>();

		distanceDurationView = (TextView) travellingModeRootView
				.findViewById(R.id.tm_dist_dur_view);
		distanceDurationView.setVisibility(View.INVISIBLE);

		travellingModeActivity = getActivity();

		travellingModeNames = new String[] { "Driving", "Walking", "Bicycling" };
		travellingModeIcons = new int[] { R.drawable.car, R.drawable.walk,
				R.drawable.bicycle };

		travellingModeDrawerLayout = (DrawerLayout) travellingModeRootView
				.findViewById(R.id.travelling_mode_layout);
		travellingModeListview = (ListView) travellingModeRootView
				.findViewById(R.id.travelling_mode_sliding_menu);
		travellingModeDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		travellingModeDrawerLayout.setBackgroundColor(Color.WHITE);

		TravellingModeSlidingMenuAdapter menuAdapter = new TravellingModeSlidingMenuAdapter(
				getSherlockActivity(), travellingModeNames, travellingModeIcons);

		travellingModeListview.setAdapter(menuAdapter);

		travellingModeDrawerToggle = new ActionBarDrawerToggle(
				getSherlockActivity(), travellingModeDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

		};
		travellingModeDrawerLayout
				.setDrawerListener(travellingModeDrawerToggle);

		travellingModeListview
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						if (travellingModeMarkerLocations.size() == 0) {

							travellingModeDrawerLayout
									.closeDrawer(travellingModeListview);
							Toast.makeText(getSherlockActivity(),
									"No points on the map!", Toast.LENGTH_LONG)
									.show();

						} else if (travellingModeMarkerLocations.size() >= 2) {

							if (position == 0) {
								checkedView = 1;
								progressDialog = new ProgressDialog(
										getSherlockActivity());

								progressDialog.setMessage("Loading...");
								progressDialog.setCancelable(false);
								progressDialog.show();

								travelling_mode = 1;

								AutoCompleteDirectionsActivity.travelling_mode = 0;

								if (travellingModeMarkerLocations.size() >= 2) {
									LatLng origin = travellingModeMarkerLocations
											.get(0);

									LatLng dest = travellingModeMarkerLocations
											.get(1);

									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest,
													travelling_mode);

									directionsdownloadTask = new DirectionsDownloadTask();
									directionsdownloadTask.execute(url);

									travellingModeDrawerLayout
											.closeDrawer(travellingModeListview);
								}
							} else if (position == 1) {
								checkedView = 1;
								progressDialog = new ProgressDialog(
										getSherlockActivity());

								progressDialog.setMessage("Loading...");
								progressDialog.setCancelable(false);
								progressDialog.show();

								travelling_mode = 2;

								AutoCompleteDirectionsActivity.travelling_mode = 0;

								if (travellingModeMarkerLocations.size() >= 2) {
									LatLng origin = travellingModeMarkerLocations
											.get(0);
									LatLng dest = travellingModeMarkerLocations
											.get(1);

									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest,
													travelling_mode);

									directionsdownloadTask = new DirectionsDownloadTask();
									directionsdownloadTask.execute(url);

									travellingModeDrawerLayout
											.closeDrawer(travellingModeListview);
								}

							} else if (position == 2) {
								checkedView = 1;
								progressDialog = new ProgressDialog(
										getSherlockActivity());

								progressDialog.setMessage("Loading...");
								progressDialog.setCancelable(false);
								progressDialog.show();

								travelling_mode = 3;

								AutoCompleteDirectionsActivity.travelling_mode = 0;

								if (travellingModeMarkerLocations.size() >= 2) {
									LatLng origin = travellingModeMarkerLocations
											.get(0);
									LatLng dest = travellingModeMarkerLocations
											.get(1);

									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest,
													travelling_mode);

									directionsdownloadTask = new DirectionsDownloadTask();
									directionsdownloadTask.execute(url);

									travellingModeDrawerLayout
											.closeDrawer(travellingModeListview);

								}
							}

						}
					}
				});

		travellingModeGoogleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {

				if (travellingModeMarkerLocations.size() > 1) {
					travellingModeMarkerLocations.clear();
					travellingModeGoogleMap.clear();
				}

				travellingModeMarkerLocations.add(arg0);

				DirectionsMarkers.drawStartStopMarkers();

			}
		});

		trafficAccidentButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {

							LatLngBounds bounds = travellingModeGoogleMap
									.getProjection().getVisibleRegion().latLngBounds;
							if (getAreaInTheScreen(bounds) < 5000000) {
								new ParseTask(travellingModeGoogleMap)
										.execute(bingServerUrlBuilder(bounds));
							} else {
								travellingModeGoogleMap.clear();

								if (travellingModeMarkerLocations.size() != 0
										&& travellingModeMarkerLocations.size() != 1) {

									LatLng origin = travellingModeMarkerLocations
											.get(0);
									LatLng dest = travellingModeMarkerLocations
											.get(1);

									DirectionsMarkers.drawStartStopMarkers();

									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest, 1);

									DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();
									directionsdownloadTask.execute(url);

								} else if (travellingModeMarkerLocations.size() == 1) {

									DirectionsMarkers.drawStartStopMarkers();
								} else {

								}

							}

						} else {
							travellingModeGoogleMap.clear();

							if (travellingModeMarkerLocations.size() != 0
									&& travellingModeMarkerLocations.size() != 1) {
								LatLng origin = travellingModeMarkerLocations
										.get(0);
								LatLng dest = travellingModeMarkerLocations
										.get(1);

								DirectionsMarkers.drawStartStopMarkers();

								String url = DirectionsParserTask
										.getDirectionsUrl(origin, dest, 1);

								DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();
								directionsdownloadTask.execute(url);
							} else if (travellingModeMarkerLocations.size() != 1) {
								DirectionsMarkers.drawStartStopMarkers();
							}

						}
					}
				});

		travellingModeGoogleMap
				.setOnCameraChangeListener(new OnCameraChangeListener() {
					@Override
					public void onCameraChange(CameraPosition position) {

						if (trafficAccidentButton.getText().equals(
								"Accidents On")) {
							LatLngBounds bounds = travellingModeGoogleMap
									.getProjection().getVisibleRegion().latLngBounds;
							if (getAreaInTheScreen(bounds) < 5000000) {

								new ParseTask(travellingModeGoogleMap)
										.execute(bingServerUrlBuilder(bounds));
							} else {
								travellingModeGoogleMap.clear();

								if (travellingModeMarkerLocations.size() != 0) {
									LatLng origin = travellingModeMarkerLocations
											.get(0);
									LatLng dest = travellingModeMarkerLocations
											.get(1);

									DirectionsMarkers.drawStartStopMarkers();

									String url = DirectionsParserTask
											.getDirectionsUrl(origin, dest, 1);

									DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();
									directionsdownloadTask.execute(url);

								}
							}
						} else {

						}

					}

				});

		return travellingModeRootView;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == AutoCompleteDirectionsActivity.RESULT_CODE) {

			String from = data.getExtras().getString("from");
			String to = data.getExtras().getString("to");

			new DirectionsFetcher(from, to).execute();

		}

		else if (resultCode == BookmarksFragment.RESULT_CODE) {

			double originLatitude = data.getExtras()
					.getDouble("originLatitude");
			double originLongitude = data.getExtras().getDouble(
					"originLongitude");
			double destLatitude = data.getExtras().getDouble("destLatitude");
			double destLongitude = data.getExtras().getDouble("destLongitude");

			LatLng origin = new LatLng(originLatitude, originLongitude);

			LatLng dest = new LatLng(destLatitude, destLongitude);

			clearMarkers();
			travellingModeMarkerLocations.add(0, origin);
			travellingModeMarkerLocations.add(1, dest);
			DirectionsMarkers.drawStartStopMarkers();
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					origin, 7);
			TravellingModeFragment.travellingModeGoogleMap
					.animateCamera(cameraUpdate);

			String url = DirectionsParserTask.getDirectionsUrl(origin, dest, 1);

			DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

			directionsdownloadTask.execute(url);

		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		travellingModeDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		travellingModeDrawerToggle.onConfigurationChanged(newConfig);
	}

	// ****** external methods ******

	private String bingServerUrlBuilder(LatLngBounds bounds) {

		double northLat = bounds.northeast.latitude;
		double northLong = bounds.northeast.longitude;
		double southLat = bounds.southwest.latitude;
		double southLong = bounds.southwest.longitude;

		bingServerCompleteUrl = bingServerAccidentUrl
				+ String.valueOf(southLat) + "," + String.valueOf(southLong)
				+ "," + String.valueOf(northLat) + ","
				+ String.valueOf(northLong) + "?key=" + bingAPIKey;
		return bingServerCompleteUrl;

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

	public static void clearMarkers() {

		travellingModeGoogleMap.clear();
		travellingModeMarkerLocations.clear();
		distanceDurationView.setVisibility(View.INVISIBLE);

	}

}