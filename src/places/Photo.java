package places;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {

	// Width of the Photo
	int photoWidth = 0;

	// Height of the Photo
	int photoHeight = 0;

	// Reference of the photo to be used in Google Web Services
	public String photoReference = "";

	// Attributions of the photo
	// Attribution is a Parcelable class
	Attribution[] attributions = {};

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(photoWidth);
		dest.writeInt(photoHeight);
		dest.writeString(photoReference);
		dest.writeParcelableArray(attributions, 0);

	}

	public Photo() {
	}

	/** Initializing Photo object from Parcel object */
	private Photo(Parcel in) {
		this.photoWidth = in.readInt();
		this.photoHeight = in.readInt();
		this.photoReference = in.readString();
		this.attributions = (Attribution[]) in
				.readParcelableArray(Attribution.class.getClassLoader());
	}

	/** Generates an instance of Place class from Parcel */
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source);
		}

		@Override
		public Photo[] newArray(int size) {
			
			return null;
		}
	};

}
