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

	static String mode = "mode=driving";
	String distance = "";
	String duration = "";

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

		ArrayList<LatLng> points = new ArrayList<LatLng>();
		PolylineOptions lineOptions = new PolylineOptions();
		HashMap<String, String> point = new HashMap<String, String>();

		try {

			if (result != null) {
				// Traversing through all the routes
				for (int i = 0; i < result.size(); i++) {

					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);

					// Fetching all the points in i-th route
					for (int j = 0; j < path.size(); j++) {
						point = path.get(j);

						if (j == 0) { // Get distance from the list
							distance = (String) point.get("distance");
							continue;
						} else if (j == 1) { // Get duration from the list
							duration = (String) point.get("duration");
							continue;
						}

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						points.add(position);
					}

					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(2);

					// Changing the color polyline according to the mode
					if (TravellingModeFragment.travelling_mode == 1)
						lineOptions.color(Color.RED);
					else if (TravellingModeFragment.travelling_mode == 2)
						lineOptions.color(Color.GREEN);
					else if (TravellingModeFragment.travelling_mode == 3)
						lineOptions.color(Color.BLUE);

					// Drawing polyline in the Google Map for the i-th route
					TravellingModeFragment.googleMap.addPolyline(lineOptions);

				}
			}

			else {
				Toast.makeText(TabActivity.mainContext,
						"No points on the map!", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {

			if (result.size() < 1) {
				Toast.makeText(TabActivity.mainContext, "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

	}

	public static String getDirectionsUrl(LatLng origin, LatLng dest,
			int travelling_mode) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		if (travelling_mode == 1) {
			mode = "mode=driving";

		} else if (travelling_mode == 2) {
			mode = "mode=bicycling";

		} else if (travelling_mode == 3) {
			mode = "mode=walking";

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
