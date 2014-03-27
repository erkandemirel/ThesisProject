package places;

import android.os.Parcel;
import android.os.Parcelable;

public class Attribution implements Parcelable {

	// Attribution of the photo
	String htmlAttribution = "";

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(htmlAttribution);
	}

	public Attribution() {
	}

	/** Initializing Attribution object from Parcel object */
	private Attribution(Parcel in) {
		this.htmlAttribution = in.readString();
	}

	/** Generates an instance of Attribution class from Parcel */
	public static final Parcelable.Creator<Attribution> CREATOR = new Parcelable.Creator<Attribution>() {

		@Override
		public Attribution createFromParcel(Parcel source) {
			return new Attribution(source);
		}

		@Override
		public Attribution[] newArray(int size) {

			return null;
		}
	};
}
