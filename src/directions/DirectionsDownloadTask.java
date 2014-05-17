package directions;

import android.os.AsyncTask;
import android.util.Log;
import autocompletetext.AutoCompletePlaceDownloadTask;

public class DirectionsDownloadTask extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// For storing data from web service
		String data = "";

		try {
			// Fetching the data from web service
			data = AutoCompletePlaceDownloadTask.downloadUrl(params[0]);
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}
		return data;
	}

	@Override
	protected void onPostExecute(String result) {
		
		super.onPostExecute(result);

		DirectionsParserTask directionsParserTask = new DirectionsParserTask();

		// Invokes the thread for parsing the JSON data
		directionsParserTask.execute(result);
	}

}
