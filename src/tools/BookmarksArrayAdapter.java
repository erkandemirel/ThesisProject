package tools;

import java.util.List;

import com.example.navigation.R;

import database.BookmarksItem;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		BookmarksItem bookmarksItem = getItem(position);
		String bookmarksItemTitle = bookmarksItem.getBookmarksItemTitle();
		String bookmarksItemAddress = bookmarksItem.getBookmarksItemAddress();

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
					.findViewById(R.id.bookmarksAddressView);

			bookmarksItemView.setTag(holder);
		} else {
			holder = (ViewHolder) bookmarksItemView.getTag();
		}

		holder.bookmarksTitleView.setText(bookmarksItemTitle);
		holder.bookmarksAddressView.setText(bookmarksItemAddress);

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
