package com.example.controller;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.view.FindNearbyPlacesFragment;
import com.example.view.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacesParserTask extends AsyncTask<String, Integer, Place[]> {

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

			double lat = Double.parseDouble(place.placeLatitude);

			double lng = Double.parseDouble(place.placeLongitude);

			LatLng latLng = new LatLng(lat, lng);

			Marker m = drawMarker(latLng);

			FindNearbyPlacesFragment.nearPlacesReference.put(m.getId(), place);
		}
	}

	public static Marker drawMarker(LatLng latLng) {

		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.position(latLng);

		if (FindNearbyPlacesFragment.placeType.equals("airport")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.airport_marker));
		} else if (FindNearbyPlacesFragment.placeType.equals("bank")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.bank_marker));
		} else if (FindNearbyPlacesFragment.placeType.equals("bus_station")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.bus_marker));
		} else if (FindNearbyPlacesFragment.placeType.equals("hospital")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.hospital_marker));
		} else if (FindNearbyPlacesFragment.placeType.equals("mosque")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.mosque_marker));
		} else if (FindNearbyPlacesFragment.placeType.equals("restaurant")) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.restaurant_marker));
		}

		Marker m = FindNearbyPlacesFragment.nearbyPlacesGoogleMap
				.addMarker(markerOptions);

		return m;
	}
}
