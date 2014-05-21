package com.example.controller;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.BookmarksItem;
import com.example.view.AutoCompleteDirectionsActivity;
import com.example.view.BookmarksFragment;
import com.example.view.R;
import com.example.view.TabActivity;

public class BookmarksArrayAdapter extends ArrayAdapter<BookmarksItem> {

	int resource;
	Context context;
	List<BookmarksItem> bookmarksItemList;
	private SparseBooleanArray mSelectedItemsIds;
	Bundle bundle;
	public static final int RESULT_CODE = 1234;
	GPSTracker gps;

	public BookmarksArrayAdapter(Context context, int resource,
			List<BookmarksItem> bookmarksItemList) {
		super(context, resource, bookmarksItemList);

		mSelectedItemsIds = new SparseBooleanArray();
		this.resource = resource;
		this.context = context;
		this.bookmarksItemList = bookmarksItemList;

	}

	private class ViewHolder {
		TextView bookmarksTitleView;
		TextView bookmarksAddressView;
		ImageView bookmarksRouteView;
	}

	@SuppressLint("UseValueOf")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		BookmarksItem bookmarksItem = getItem(position);
		String bookmarksItemTitle = bookmarksItem.getBookmarksItemTitle();
		final String bookmarksItemAddress = bookmarksItem
				.getBookmarksItemAddress();

		View bookmarksItemView = convertView;
		ViewHolder holder;

		if (bookmarksItemView == null) {
			String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(inflaterService);
			bookmarksItemView = inflater.inflate(resource, null);
			holder = new ViewHolder();
			holder.bookmarksTitleView = (TextView) bookmarksItemView
					.findViewById(R.id.bookmarksTitleView);
			holder.bookmarksAddressView = (TextView) bookmarksItemView
					.findViewById(R.id.addressTitleView);
			holder.bookmarksRouteView = (ImageView) bookmarksItemView
					.findViewById(R.id.bookmarksRouteView);

			bookmarksItemView.setTag(holder);
		} else {
			holder = (ViewHolder) bookmarksItemView.getTag();
		}
		holder.bookmarksRouteView.setTag(new Integer(position));

		holder.bookmarksTitleView.setText(bookmarksItemTitle);
		holder.bookmarksRouteView.setImageResource(R.drawable.bookmarksroute);
		holder.bookmarksAddressView.setText(bookmarksItemAddress);

		holder.bookmarksRouteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				gps = new GPSTracker(TabActivity.mainContext);
				AutoCompleteDirectionsActivity.travelling_mode = 4;

				double originLatitude = gps.getLatitude();
				double originLongitude = gps.getLongitude();

				int position = (Integer) v.getTag();

				BookmarksItem item = (BookmarksItem) BookmarksFragment.bookmarksListView
						.getItemAtPosition(position);

				double destLatitude = item.getBookmarksItemLatitude();

				double destLongitude = item.getBookmarksItemLongitude();
			
				Intent bookmarksIntent = new Intent();
				bookmarksIntent.putExtra("originLatitude", originLatitude);
				bookmarksIntent.putExtra("originLongitude", originLongitude);
				bookmarksIntent.putExtra("destLatitude", destLatitude);
				bookmarksIntent.putExtra("destLongitude", destLongitude);

				BookmarksFragment.bookmarksActivity.setResult(RESULT_CODE,
						bookmarksIntent);

				BookmarksFragment.bookmarksActivity.finish();

			}
		});

		return bookmarksItemView;

	}

	// **** External Methods ****

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

}
