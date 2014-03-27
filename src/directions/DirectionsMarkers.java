package directions;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import fragments.TravellingModeFragment;

public class DirectionsMarkers {
	
	// Drawing Start and Stop locations
	public static void drawStartStopMarkers() {

		for (int i = 0; i < TravellingModeFragment.markerPoints.size(); i++) {

			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();

			// Setting the position of the marker
			options.position(TravellingModeFragment.markerPoints.get(i));

			/**
			 * For the start location, the color of marker is GREEN and for the
			 * end location, the color of marker is RED.
			 */
			if (i == 0) {
				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			} else if (i == 1) {
				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			}

			// Add new marker to the Google Map Android API V2
			TravellingModeFragment.googleMap.addMarker(options);
		}
	}

}
