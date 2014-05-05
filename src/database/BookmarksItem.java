package database;

import java.io.Serializable;

public class BookmarksItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bookmarksItemID;
	private String bookmarksItemTitle;
	private String bookmarksItemAddress;

	public BookmarksItem(int bookmarksItemID, String bookmarksItemTitle,
			String bookmarksItemAddress) {

		this.bookmarksItemID = bookmarksItemID;
		this.bookmarksItemTitle = bookmarksItemTitle;
		this.bookmarksItemAddress = bookmarksItemAddress;
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

}
