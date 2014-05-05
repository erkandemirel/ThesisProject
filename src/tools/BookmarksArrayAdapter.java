package tools;

import java.util.List;

import com.example.navigation.R;

import database.BookmarksItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BookmarksArrayAdapter extends ArrayAdapter<BookmarksItem> {

	int resource;
	Context context;
	List<BookmarksItem> bookmarksItemList;

	public BookmarksArrayAdapter(Context context, int resource,
			List<BookmarksItem> objects) {
		super(context, resource, objects);

		this.resource = resource;
		this.context = context;
		this.bookmarksItemList = objects;

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

}
