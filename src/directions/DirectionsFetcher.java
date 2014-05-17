package directions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import tools.GPSTracker;
import android.os.AsyncTask;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;
import fragments.TravellingModeFragment;

public class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {
	private String origin;
	private String destination;
	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp
			.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private ArrayList<LatLng> latLngs;
	GPSTracker gps = new GPSTracker(TabActivity.mainContext);
	DirectionsReverseGeocodingTask reverseGeocodingTask = new DirectionsReverseGeocodingTask(
			gps);
	DirectionsDownloadTask directionsdownloadTask;

	public DirectionsFetcher(String origin, String destination) {
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		TravellingModeFragment.clearMarkers();
		TravellingModeFragment.travellingModeActivity
				.setProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

	protected Void doInBackground(URL... urls) {
		if (origin.equalsIgnoreCase("My Location")) {
			if (gps.canGetLocation()) {
				double myLocationLatitude = gps.getLatitude();
				double myLocationLongitude = gps.getLongitude();
				LatLng myLocation = new LatLng(myLocationLatitude,
						myLocationLongitude);
				origin = reverseGeocodingTask.doInBackground(myLocation);
			}
		}
		try {
			HttpRequestFactory requestFactory = HTTP_TRANSPORT
					.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) {
							request.setParser(new JsonObjectParser(JSON_FACTORY));
						}
					});
			GenericUrl url = new GenericUrl(
					"http://maps.googleapis.com/maps/api/directions/json");
			url.put("origin", origin);
			url.put("destination", destination);
			url.put("sensor", false);
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			DirectionsResult directionsResult = httpResponse
					.parseAs(DirectionsResult.class);
			String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
			latLngs = (ArrayList<LatLng>) PolyUtil.decode(encodedPoints);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				latLngs.get(0), 7);
		TravellingModeFragment.travellingModeGoogleMap
				.animateCamera(cameraUpdate);
		String url = DirectionsParserTask.getDirectionsUrl(latLngs.get(0),
				latLngs.get(latLngs.size() - 1),
				TravellingModeFragment.travelling_mode);

		TravellingModeFragment.travellingModeMarkerLocations.add(0,
				latLngs.get(0));
		TravellingModeFragment.travellingModeMarkerLocations.add(1,
				latLngs.get(latLngs.size() - 1));
		DirectionsMarkers.drawStartStopMarkers();
		directionsdownloadTask = new DirectionsDownloadTask();
		directionsdownloadTask.execute(url);
	}

	public static class DirectionsResult {
		@Key("routes")
		public List<Route> routes;
	}

	public static class Route {
		@Key("overview_polyline")
		public OverviewPolyLine overviewPolyLine;
	}

	public static class OverviewPolyLine {
		@Key("points")
		public String points;
	}
}