package autocompletetext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import fragments.FindNearbyPlacesFragment;

public class AutoCompletePlaceDownloadTask extends
		AsyncTask<String, Void, String> {

	private int downloadType = 0;

	AutoCompletePlaceParserTask placesParserTask;
	AutoCompletePlaceParserTask placeDetailsParserTask;

	// Constructor
	public AutoCompletePlaceDownloadTask(int type) {
		this.downloadType = type;
	}

	@Override
	protected String doInBackground(String... params) {
		// For storing data from web service
		String data = "";

		try {
			// Fetching the data from web service
			data = downloadUrl(params[0]);
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}
		return data;
	}

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);

		switch (downloadType) {
		case FindNearbyPlacesFragment.PLACES:
			// Creating ParserTask for parsing Google Places
			placesParserTask = new AutoCompletePlaceParserTask(
					FindNearbyPlacesFragment.PLACES);

			// Start parsing google places json data
			// This causes to execute doInBackground() of ParserTask class
			placesParserTask.execute(result);

			break;

		case FindNearbyPlacesFragment.PLACES_DETAILS:
			// Creating ParserTask for parsing Google Places
			placeDetailsParserTask = new AutoCompletePlaceParserTask(
					FindNearbyPlacesFragment.PLACES_DETAILS);

			// Starting Parsing the JSON string
			// This causes to execute doInBackground() of ParserTask class
			placeDetailsParserTask.execute(result);

		}
	}

	/** A method to download json data from url */
	public static String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

}