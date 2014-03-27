package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AnimatingFragment extends SherlockMapFragment {

	private List<Marker> markers = new ArrayList<Marker>();

	private GoogleMap googleMap;

	int currentPoint;
	
	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};


	public static AnimatingFragment newInstance(int position, String title) {
		AnimatingFragment fragment = new AnimatingFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("title", title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		handler.postDelayed(runner, random.nextInt(2000));
		
		googleMap = getMap();

		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				addMarkerToMap(latLng);
			}

		});
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.animation_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		} else if (item.getItemId() == R.id.action_bar_start_animation) {
			startAnimation();
		}

		return true;
	}

	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions()
				.position(latLng).title("title").snippet("snippet"));
		markers.add(marker);

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

	private void highLightMarker(int index) {
		highLightMarker(markers.get(index));
	}

	private void highLightMarker(Marker marker) {
		marker.setIcon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker.showInfoWindow();
	}
	
	private void startAnimation() {
		resetMarkers();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers
				.get(0).getPosition(), 16), 5000,
				simpleAnimationCancelableCallback);

		currentPoint = 0 - 1;
	}

	private void resetMarkers() {
		for (Marker marker : this.markers) {
			marker.setIcon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
	}

	CancelableCallback simpleAnimationCancelableCallback = new CancelableCallback() {

		@Override
		public void onCancel() {

		}

		@Override
		public void onFinish() {
			if (++currentPoint < markers.size()) {

				LatLng targetLatLng = markers.get(currentPoint).getPosition();

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(targetLatLng)
						.tilt(currentPoint < markers.size() - 1 ? 90 : 0)
						.zoom(googleMap.getCameraPosition().zoom).build();

				googleMap.animateCamera(
						CameraUpdateFactory.newCameraPosition(cameraPosition),
						3000, simpleAnimationCancelableCallback);

				highLightMarker(currentPoint);

			}

		}

	};

}
