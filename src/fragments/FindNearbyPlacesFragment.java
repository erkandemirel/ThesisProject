package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import places.Place;
import places.PlaceDialogFragment;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
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

public class FindNearbyPlacesFragment extends SherlockMapFragment{

	public static GoogleMap googleMap;
	
	private SupportMapFragment fragment;

	private List<Marker> markers = new ArrayList<Marker>();

	private static final float UNDEFINED_COLOR = -1;

	public static final int PLACES = 0;

	public static final int PLACES_DETAILS = 1;

	Place[] nearPlaces;

	String[] nearPlacesType;

	String[] nearPlacesName;

	public static LatLng latLng;

	public static AutoCompleteTextView textViewPlaces;

	public static HashMap<String, Place> nearPlacesReference;

	AutoCompletePlaceDownloadTask placesDownloadTask;
	AutoCompletePlaceDownloadTask placeDetailsDownloadTask;
	AutoCompletePlaceParserTask placesParserTask;
	AutoCompletePlaceParserTask placeDetailsParserTask;

	private DrawerLayout drawlayout = null;
	private ListView listview = null;
	private ActionBarDrawerToggle actbardrawertoggle = null;
	private String[] placeNames;
	private int[] placeIcons;

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

	    Fragment xmlFragment = fm.findFragmentById(R.id.nearby_places_map);
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

		View view = inflater.inflate(R.layout.nearby_places, container,
				false);

		FragmentManager fragmentManager = getFragmentManager();
		
		fragment= (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.nearby_places_map);
		
		

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.commit();

		
		googleMap = fragment.getMap();
		

		if (googleMap != null) {
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.setMyLocationEnabled(true);
		}

		nearPlacesReference = new HashMap<String, Place>();

		placeNames = new String[] { "Airport", "ATM", "Bank", "Bus Station",
				"Cinema", "Hospital", "Mosque", "Restaurant" };
		placeIcons = new int[] { R.drawable.airport1, R.drawable.atm,
				R.drawable.bank1, R.drawable.bus, R.drawable.cinema2,
				R.drawable.hospital1, R.drawable.mosque, R.drawable.restaurant3 };
		drawlayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

		listview = (ListView) view.findViewById(R.id.left_drawer);

		drawlayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawlayout.setBackgroundColor(Color.WHITE);
		NearbyPlacesSlidingMenuAdapter menuAdapter = new NearbyPlacesSlidingMenuAdapter(
				getSherlockActivity(), placeNames, placeIcons);
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
					getNearbyPlaces("airport");
					drawlayout.closeDrawer(listview);
				} else if (position == 1) {
					getNearbyPlaces("atm");
					drawlayout.closeDrawer(listview);
				} else if (position == 2) {
					getNearbyPlaces("bank");
					drawlayout.closeDrawer(listview);
				} else if (position == 3) {
					getNearbyPlaces("bus_station");
					drawlayout.closeDrawer(listview);
				} else if (position == 4) {
					getNearbyPlaces("cinema");
					drawlayout.closeDrawer(listview);
				} else if (position == 5) {
					getNearbyPlaces("hospital");
					drawlayout.closeDrawer(listview);
				} else if (position == 6) {
					getNearbyPlaces("mosque");
					drawlayout.closeDrawer(listview);
				} else if (position == 7) {
					getNearbyPlaces("restaurant");
					drawlayout.closeDrawer(listview);
				}

			}
		});
		
		
		 // Handling screen rotation
        if(savedInstanceState !=null) {

            // Removes all the existing links from marker id to place object
        	nearPlacesReference.clear();

            //If near by places are already saved
            if(savedInstanceState.containsKey("places")){

                // Retrieving the array of place objects
            	nearPlaces = (Place[]) savedInstanceState.getParcelableArray("places");

                // Traversing through each near by place object
                for(int i=0;i<nearPlaces.length;i++){

                    // Getting latitude and longitude of the i-th place
                    LatLng point = new LatLng(Double.parseDouble(nearPlaces[i].placeLatitude),
                    Double.parseDouble(nearPlaces[i].placeLongitude));

                    // Drawing the marker corresponding to the i-th place
                    Marker m = addMarker(point,UNDEFINED_COLOR);

                    // Linkng i-th place and its marker id
                    nearPlacesReference.put(m.getId(), nearPlaces[i]);
                }
            }

            // If a touched location is already saved
            if(savedInstanceState.containsKey("location")){

                // Retrieving the touched location and setting in member variable
            	latLng = (LatLng) savedInstanceState.getParcelable("location");

               // Drawing a marker at the touched location
               addMarker(latLng, BitmapDescriptorFactory.HUE_GREEN);
           }
       }


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

		return view;

	}
	
	 /**
	    * A callback function, executed on screen rotation
	    */
	    @Override
	public void onSaveInstanceState(Bundle outState) {
	 
	        // Saving all the near by places objects
	        if(nearPlaces!=null)
	            outState.putParcelableArray("places", nearPlaces);
	 
	        // Saving the touched location
	        if(latLng!=null)
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
