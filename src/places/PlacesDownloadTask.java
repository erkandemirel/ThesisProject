package places;

import android.os.AsyncTask;
import android.util.Log;
import autocompletetext.AutoCompletePlaceDownloadTask;

public class PlacesDownloadTask extends AsyncTask<String, Integer, String> {

	
	String data = null;

	@Override
	protected String doInBackground(String... params) {
		try {
			data = AutoCompletePlaceDownloadTask.downloadUrl(params[0]);
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}
		return data;
	}

	@Override
	protected void onPostExecute(String result) {
		PlacesParserTask parserTask = new PlacesParserTask();
		parserTask.execute(result);
	}
	
	

}
