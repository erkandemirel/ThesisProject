package fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import places.Photo;
import places.Place;

import tools.GPSTracker;

import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.model.LatLng;

import directions.DirectionsDownloadTask;
import directions.DirectionsParserTask;
import fragments.AddDatabaseFragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

@SuppressLint("ValidFragment")
public class PlaceDialogFragment extends DialogFragment {
	TextView photoCountText = null;
	TextView placeVicinityText = null;
	ViewFlipper placeFlipper = null;
	public static Place placeObject = null;
	DisplayMetrics metrics = null;
	Button addButton;
	Button routeButton;

	GPSTracker gps;

	public static int travelling_mode = 0;

	public PlaceDialogFragment() {
		super();
	}

	@SuppressWarnings("static-access")
	public PlaceDialogFragment(Place place, DisplayMetrics dm) {
		super();
		this.placeObject = place;
		this.metrics = dm;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.places_photo_view, null);

		// Getting reference to ViewFlipper
		placeFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

		// Getting reference to TextView to display photo count
		photoCountText = (TextView) v.findViewById(R.id.tv_photos_count);

		// Getting reference to TextView to display place vicinity
		placeVicinityText = (TextView) v.findViewById(R.id.tv_vicinity);

		addButton = (Button) v.findViewById(R.id.place_add_database);

		routeButton = (Button) v.findViewById(R.id.place_direction);

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DisplayMetrics dm = new DisplayMetrics();

				WindowManager windowManager = (WindowManager) TabActivity.mainContext
						.getSystemService(TabActivity.WINDOW_SERVICE);

				Display display = windowManager.getDefaultDisplay();

				display.getMetrics(dm);

				AddDatabaseFragment addDatabaseFragment = new AddDatabaseFragment();

				FragmentManager fm = getFragmentManager();

				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add(addDatabaseFragment, "TAG");

				fragmentTransaction.commit();

			}
		});

		routeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gps = new GPSTracker(TabActivity.mainContext);

				travelling_mode = 1;

				if (gps.canGetLocation()) {

					double originLatitude = gps.getLatitude();
					double originLongitude = gps.getLongitude();

					double latitude = 0;
					double longitude = 0;

					try {

						latitude = Double
								.parseDouble(PlaceDialogFragment.placeObject.placeLatitude);
					} catch (NumberFormatException e) {

					}

					try {

						longitude = Double
								.parseDouble(PlaceDialogFragment.placeObject.placeLongitude);
					} catch (NumberFormatException e) {

					}

					LatLng origin = new LatLng(originLatitude, originLongitude);

					LatLng dest = new LatLng(latitude, longitude);

					String url = DirectionsParserTask.getDirectionsUrl(origin,
							dest, 1);

					DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

					directionsdownloadTask.execute(url);
				} else {

					gps.showSettingsAlert();
				}

			}
		});

		if (placeObject != null) {

			// Setting the title for the Dialog Fragment
			getDialog().setTitle(placeObject.placeName);

			// Array of references of the photos
			Photo[] photos = placeObject.placePhoto;

			// Setting Photos count
			photoCountText.setText("Photos available : " + photos.length);

			// Setting the vicinity of the place
			placeVicinityText.setText(placeObject.vicinity);

			// Creating an array of ImageDownloadTask to download photos
			ImageDownloadTask[] imageDownloadTask = new ImageDownloadTask[photos.length];

			int width = (int) (metrics.widthPixels * 3) / 4;
			int height = (int) (metrics.heightPixels * 1) / 2;

			String url = "https://maps.googleapis.com/maps/api/place/photo?";
			String key = "key=AIzaSyC-CiTPvezMf-xewmsVJZp8P8PcnWkSJow";
			String sensor = "sensor=true";
			String maxWidth = "maxwidth=" + width;
			String maxHeight = "maxheight=" + height;
			url = url + "&" + key + "&" + sensor + "&" + maxWidth + "&"
					+ maxHeight;

			// Traversing through all the photoreferences
			for (int i = 0; i < photos.length; i++) {
				// Creating a task to download i-th photo
				imageDownloadTask[i] = new ImageDownloadTask();

				String photoReference = "photoreference="
						+ photos[i].photoReference;

				// URL for downloading the photo from Google Services
				url = url + "&" + photoReference;

				// Downloading i-th photo from the above url
				imageDownloadTask[i].execute(url);
			}
		}

		return v;
	}

	private Bitmap downloadImage(String strUrl) throws IOException {
		Bitmap bitmap = null;
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);

			/** Creating an http connection to communcate with url */
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			/** Connecting to url */
			urlConnection.connect();

			/** Reading data from url */
			iStream = urlConnection.getInputStream();

			/** Creating a bitmap from the stream returned from the url */
			bitmap = BitmapFactory.decodeStream(iStream);

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
		}
		return bitmap;
	}

	private class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
		Bitmap bitmap = null;

		@Override
		protected Bitmap doInBackground(String... url) {
			try {
				// Starting image download
				bitmap = downloadImage(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// Creating an instance of ImageView to display the downloaded image
			ImageView iView = new ImageView(getActivity().getBaseContext());

			// Setting the downloaded image in ImageView
			iView.setImageBitmap(result);

			// Adding the ImageView to ViewFlipper
			placeFlipper.addView(iView);

		}
	}

}
