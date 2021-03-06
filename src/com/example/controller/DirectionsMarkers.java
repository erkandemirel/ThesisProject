package com.example.controller;



import com.example.view.R;
import com.example.view.TravellingModeFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;


public class DirectionsMarkers {
	
	// Drawing Start and Stop locations
	public static void drawStartStopMarkers() {

		for (int i = 0; i < TravellingModeFragment.travellingModeMarkerLocations.size(); i++) {

			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();

			// Setting the position of the marker
			options.position(TravellingModeFragment.travellingModeMarkerLocations.get(i));

			/**
			 * For the start location, the color of marker is GREEN and for the
			 * end location, the color of marker is RED.
			 */
			if (i == 0) {
				options.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.marker_icon));
			} else if (i == 1) {
				options.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.marker_icon2));
			}

			// Add new marker to the Google Map Android API V2
			TravellingModeFragment.travellingModeGoogleMap.addMarker(options);
		}
	}

}
