package places;

import java.util.HashMap;

import org.json.JSONObject;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fragments.FindNearbyPlacesFragment;

import android.os.AsyncTask;
import android.util.Log;

public class PlacesParserTask extends AsyncTask<String, Integer, Place[]> {

	// Specifies the drawMarker() to draw the marker with default color
	private static final float UNDEFINED_COLOR = -1;

	HashMap<String, Place> nearPlacesReference = new HashMap<String, Place>();

	JSONObject jObject;

	PlaceJSONParser placeJsonParser;

	@Override
	protected Place[] doInBackground(String... params) {

		Place[] places = null;

		placeJsonParser = new PlaceJSONParser();

		try {
			jObject = new JSONObject(params[0]);

			places = placeJsonParser.parse(jObject);

		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		return places;
	}

	@Override
	protected void onPostExecute(Place[] result) {

		for (int i = 0; i < result.length; i++) {
			Place place = result[i];

			// Getting latitude of the place
			double lat = Double.parseDouble(place.placeLatitude);

			// Getting longitude of the place
			double lng = Double.parseDouble(place.placeLongitude);

			LatLng latLng = new LatLng(lat, lng);

			Marker m = drawMarker(latLng, UNDEFINED_COLOR);

			// Adding place reference to HashMap with marker id as HashMap
			// key
			// to get its reference in infowindow click event listener
			nearPlacesReference.put(m.getId(), place);
		}
	}

	// Drawing marker at latLng with color

	public static Marker drawMarker(LatLng latLng, float color) {

		// Creating a marker
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting the position for the marker
		markerOptions.position(latLng);

		if (color != UNDEFINED_COLOR)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));

		// Placing a marker on the touched position
		Marker m = FindNearbyPlacesFragment.googleMap.addMarker(markerOptions);

		return m;
	}
}
