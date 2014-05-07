package database;

import java.io.Serializable;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class BookmarksItem implements Serializable, Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bookmarksItemID;
	private String bookmarksItemTitle;
	private String bookmarksItemAddress;

	public BookmarksItem(int bookmarksItemID, String bookmarksItemTitle,
			String bookmarksItemAddress) {

		this.setBookmarksItemID(bookmarksItemID);
		this.setBookmarksItemTitle(bookmarksItemTitle);
		this.setBookmarksItemAddress(bookmarksItemAddress);
	}

	public BookmarksItem() {

	}

	public int getBookmarksItemID() {
		return bookmarksItemID;
	}

	public void setBookmarksItemID(int bookmarksItemID) {
		this.bookmarksItemID = bookmarksItemID;
	}

	public String getBookmarksItemTitle() {
		return bookmarksItemTitle;
	}

	public void setBookmarksItemTitle(String bookmarksItemTitle) {
		this.bookmarksItemTitle = bookmarksItemTitle;
	}

	public String getBookmarksItemAddress() {
		return bookmarksItemAddress;
	}

	public void setBookmarksItemAddress(String bookmarksItemAddress) {
		this.bookmarksItemAddress = bookmarksItemAddress;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}
