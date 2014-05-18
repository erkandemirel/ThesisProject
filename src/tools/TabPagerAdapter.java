package tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import fragments.BookmarksFragment;
import fragments.FindNearbyPlacesFragment;
import fragments.TravellingModeFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "Nearby Places", "Travelling Mode",
			"Bookmarks" };

	public TabPagerAdapter(FragmentManager fm) {
		super(fm);

	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {

		if (position == 0) {

			return FindNearbyPlacesFragment.newInstance(position,
					"Nearby Places");

		}

		else if (position == 1) {

			return TravellingModeFragment.newInstance(position,
					"Travelling Mode");
		}

		else if (position == 2) {

			return BookmarksFragment.newInstance(position, "Bookmarks");
		}

		return null;

	}

}