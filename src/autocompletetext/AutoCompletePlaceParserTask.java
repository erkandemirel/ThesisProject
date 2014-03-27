package autocompletetext;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.example.navigation.TabActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fragments.FindPlacesByAutoCompleteTextViewFragment;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

public class AutoCompletePlaceParserTask extends
		AsyncTask<String, Integer, List<HashMap<String, String>>> {

	int parserType = 0;

	public AutoCompletePlaceParserTask(int type) {
		this.parserType = type;
	}

	@Override
	protected List<HashMap<String, String>> doInBackground(String... params) {
		JSONObject jObject;
		List<HashMap<String, String>> list = null;

		try {
			jObject = new JSONObject(params[0]);

			switch (parserType) {
			case FindPlacesByAutoCompleteTextViewFragment.PLACES:
				AutoCompletePlaceJSONParser placeJsonParser = new AutoCompletePlaceJSONParser();
				// Getting the parsed data as a List construct
				list = placeJsonParser.parse(jObject);
				break;
			case FindPlacesByAutoCompleteTextViewFragment.PLACES_DETAILS:
				AutoCompletePlaceDetailsJSONParser placeDetailsJsonParser = new AutoCompletePlaceDetailsJSONParser();
				// Getting the parsed data as a List construct
				list = placeDetailsJsonParser.parse(jObject);
			}

		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<HashMap<String, String>> result) {

		switch (parserType) {

		case FindPlacesByAutoCompleteTextViewFragment.PLACES:
			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			SimpleAdapter adapter = new SimpleAdapter(TabActivity.mainContext,
					result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			FindPlacesByAutoCompleteTextViewFragment.textViewPlaces.setAdapter(adapter);
			break;

		case FindPlacesByAutoCompleteTextViewFragment.PLACES_DETAILS:
			HashMap<String, String> hm = result.get(0);

			// Getting latitude from the parsed data
			double latitude = Double.parseDouble(hm.get("lat"));

			// Getting longitude from the parsed data
			double longitude = Double.parseDouble(hm.get("lng"));

			// Getting reference to the SupportMapFragment of the
			// activity_main.xml

			LatLng point = new LatLng(latitude, longitude);

			CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
			CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);

			// Showing the user input location in the Google Map
			FindPlacesByAutoCompleteTextViewFragment.googleMap
					.moveCamera(cameraPosition);
			FindPlacesByAutoCompleteTextViewFragment.googleMap
					.animateCamera(cameraZoom);

			MarkerOptions options = new MarkerOptions();
			options.position(point);
			options.title("Position");
			options.snippet("Latitude:" + latitude + ",Longitude:" + longitude);

			// Adding the marker in the Google Map
			FindPlacesByAutoCompleteTextViewFragment.googleMap
					.addMarker(options);

			break;
		}
	}

}
