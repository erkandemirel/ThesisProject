package directions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.example.navigation.TabActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import fragments.TravellingModeFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

public class DirectionsParserTask extends
		AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

	static int mMode = 0;
	final int MODE_DRIVING = 0;
	final int MODE_BICYCLING = 1;
	final int MODE_WALKING = 2;

	@Override
	protected List<List<HashMap<String, String>>> doInBackground(
			String... params) {
		JSONObject jObject;
		List<List<HashMap<String, String>>> routes = null;

		try {
			jObject = new JSONObject(params[0]);
			DirectionsJSONParser parser = new DirectionsJSONParser();

			// Starts parsing data
			routes = parser.parse(jObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return routes;
	}

	@Override
	protected void onPostExecute(List<List<HashMap<String, String>>> result) {
		ArrayList<LatLng> points = null;
		PolylineOptions lineOptions = null;
		// MarkerOptions markerOptions = new MarkerOptions();

		try {
			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);

				// Changing the color polyline according to the mode
				if (mMode == MODE_DRIVING)
					lineOptions.color(Color.RED);
				else if (mMode == MODE_BICYCLING)
					lineOptions.color(Color.GREEN);
				else if (mMode == MODE_WALKING)
					lineOptions.color(Color.BLUE);

				// Drawing polyline in the Google Map for the i-th route
				TravellingModeFragment.googleMap.addPolyline(lineOptions);
			}
		} catch (Exception e) {

			if (result.size() < 1) {
				Toast.makeText(TabActivity.mainContext, "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

	}

	public static String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Travelling Mode
		String mode = "mode=driving";

		if (TravellingModeFragment.rbDriving.isChecked()) {
			mode = "mode=driving";
			mMode = 0;
		} else if (TravellingModeFragment.rbBiCycling.isChecked()) {
			mode = "mode=bicycling";
			mMode = 1;
		} else if (TravellingModeFragment.rbWalking.isChecked()) {
			mode = "mode=walking";
			mMode = 2;
		}

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

}
