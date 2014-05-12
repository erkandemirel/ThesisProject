package autocompletetext;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.example.navigation.TabActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import fragments.FindNearbyPlacesFragment;

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
			case FindNearbyPlacesFragment.PLACES:
				AutoCompletePlaceJSONParser placeJsonParser = new AutoCompletePlaceJSONParser();
				// Getting the parsed data as a List construct
				list = placeJsonParser.parse(jObject);

				break;
			case FindNearbyPlacesFragment.PLACES_DETAILS:
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

		case FindNearbyPlacesFragment.PLACES:
			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			SimpleAdapter adapter = new SimpleAdapter(TabActivity.mainContext,
					result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			FindNearbyPlacesFragment.nearbyPlacesAutoCompleteTextView.setAdapter(adapter);
			break;

		case FindNearbyPlacesFragment.PLACES_DETAILS:
			HashMap<String, String> hm = result.get(0);

			// Getting latitude from the parsed data
			double latitude = Double.parseDouble(hm.get("lat"));

			// Getting longitude from the parsed data
			double longitude = Double.parseDouble(hm.get("lng"));

			FindNearbyPlacesFragment.nearbyPlaceslatLng = new LatLng(latitude, longitude);

			CameraUpdate cameraPosition = CameraUpdateFactory
					.newLatLng(FindNearbyPlacesFragment.nearbyPlaceslatLng);

			// Showing the user input location in the Google Map
			FindNearbyPlacesFragment.nearbyPlacesGoogleMap.moveCamera(cameraPosition);

			// Adding the marker in the Google Map
			FindNearbyPlacesFragment.addMarker(FindNearbyPlacesFragment.nearbyPlaceslatLng,
					BitmapDescriptorFactory.HUE_GREEN);

			break;
		}
	}

}