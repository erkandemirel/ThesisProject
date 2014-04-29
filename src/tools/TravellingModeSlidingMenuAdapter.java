package tools;

import com.example.navigation.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TravellingModeSlidingMenuAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater inflater;
	private String[] travellingModeNames;
	private int[] travellingModeIcons;

	public TravellingModeSlidingMenuAdapter(Context context,
			String[] travellingModeNames, int[] travellingModeIcons) {
		super();
		this.context = context;
		this.travellingModeNames = travellingModeNames;
		this.travellingModeIcons = travellingModeIcons;
	}

	@Override
	public int getCount() {

		return travellingModeNames.length;
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView travellingModeName;
		ImageView travellingModeIcon;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.travelling_mode_sliding_menu,
				parent, false);
		travellingModeName = (TextView) view
				.findViewById(R.id.travellingModeNames);
		travellingModeIcon = (ImageView) view
				.findViewById(R.id.travellingModeIcon);

		travellingModeName.setText(travellingModeNames[position]);
		travellingModeIcon.setImageResource(travellingModeIcons[position]);

		return view;
	}

}
