package tools;

import java.util.List;

import com.example.navigation.AutoCompleteDirectionsActivity;
import com.example.navigation.R;

import database.BookmarksItem;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookmarksArrayAdapter extends ArrayAdapter<BookmarksItem> {

	int resource;
	Context context;
	List<BookmarksItem> bookmarksItemList;
	private SparseBooleanArray mSelectedItemsIds;

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

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

		holder.bookmarksTitleView.setText(bookmarksItemTitle);
		holder.bookmarksRouteView.setImageResource(R.drawable.bookmarksroute);
		holder.bookmarksAddressView.setText(bookmarksItemAddress);

		

		holder.bookmarksRouteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent bookmarksToDirectionActivityIntent = new Intent(context,
						AutoCompleteDirectionsActivity.class);
				bookmarksToDirectionActivityIntent.putExtra("bookmarksAddress",
						bookmarksItemAddress);
				context.startActivity(bookmarksToDirectionActivityIntent);

			}
		});

		return bookmarksItemView;

	}

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
