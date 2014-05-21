package com.example.model;

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
	private double bookmarksItemLatitude;
	private double bookmarksItemLongitude;

	public BookmarksItem(int bookmarksItemID, String bookmarksItemTitle,
			String bookmarksItemAddress,double bookmarksItemLatitude,double bookmarksItemLongitude) {

		this.setBookmarksItemID(bookmarksItemID);
		this.setBookmarksItemTitle(bookmarksItemTitle);
		this.setBookmarksItemAddress(bookmarksItemAddress);
		this.setBookmarksItemLatitude(bookmarksItemLatitude);
		this.setBookmarksItemLongitude(bookmarksItemLongitude);
		
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

	public double getBookmarksItemLatitude() {
		return bookmarksItemLatitude;
	}

	public void setBookmarksItemLatitude(double bookmarksItemLatitude) {
		this.bookmarksItemLatitude = bookmarksItemLatitude;
	}

	public double getBookmarksItemLongitude() {
		return bookmarksItemLongitude;
	}

	public void setBookmarksItemLongitude(double bookmarksItemLongitude) {
		this.bookmarksItemLongitude = bookmarksItemLongitude;
	}

}
