package tools;

import com.example.navigation.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyPlacesSlidingMenuAdapter extends BaseAdapter {

	private Context context;
	private String[] placeNames;
	private int[] placeIcons;
	private LayoutInflater inflater;

	public NearbyPlacesSlidingMenuAdapter(Context context, String[] placeNames,
			int[] placeIcons) {
		super();
		this.context = context;
		this.placeNames = placeNames;
		this.placeIcons = placeIcons;
	}

	@Override
	public int getCount() {

		return placeNames.length;
	}

	@Override
	public Object getItem(int arg0) {

		return arg0;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView placeName;
		ImageView placeIcon;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.nearby_places_sliding_menu,
				parent, false);
		placeName = (TextView) itemView.findViewById(R.id.placeName);
		placeIcon = (ImageView) itemView.findViewById(R.id.placeIcon);

		placeName.setText(placeNames[position]);
		placeIcon.setImageResource(placeIcons[position]);

		return itemView;
	}

}
