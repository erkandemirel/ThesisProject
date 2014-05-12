package places;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
	// Latitude of the place
	public String placeLatitude = "";

	// Longitude of the place
	public String placeLongitude = "";

	// Place Name
	public String placeName = "";

	// Vicinity of the place
	public String vicinity = "";

	// Photos of the place
	// Photo is a Parcelable class
	public Photo[] placePhoto = {};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Writing Place object data to Parcel */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(placeLatitude);
		dest.writeString(placeLongitude);
		dest.writeString(placeName);
		dest.writeString(vicinity);
		dest.writeParcelableArray(placePhoto, 0);
	}

	public Place() {
	}

	/** Initializing Place object from Parcel object */
	private Place(Parcel in) {
		this.placeLatitude = in.readString();
		this.placeLongitude = in.readString();
		this.placeName = in.readString();
		this.vicinity = in.readString();
		this.placePhoto = (Photo[]) in.readParcelableArray(Photo.class
				.getClassLoader());
	}

	/** Generates an instance of Place class from Parcel */
	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel source) {
			return new Place(source);
		}

		@Override
		public Place[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

}

