package com.example.navigation;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.astuetz.PagerSlidingTabStrip;
import fragments.AnimatingFragment;
import fragments.TravellingModeFragment;
import fragments.FindNearbyPlacesFragment;
import fragments.FindPlacesByAutoCompleteTextViewFragment;

public class TabActivity extends SherlockFragmentActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private TabPagerAdapter adapter;

	public static DisplayMetrics displayMetrics;

	public static FragmentManager fragmentManager;

	public static FragmentTransaction fragmentTransaction;

	public static Context mainContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mainContext = getApplicationContext();

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_tabs);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new TabPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);

		// Creating an instance of DisplayMetrics
		displayMetrics = new DisplayMetrics();

		// Getting the screen display metrics
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		// Getting a reference to Fragment Manager
		fragmentManager = getSupportFragmentManager();

		// Starting Fragment Transaction
		fragmentTransaction = fragmentManager.beginTransaction();

		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
	}

	public class TabPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Find Places", "Find Nearby Places",
				"Animation", "Travelling Mode" };

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
				Fragment fragmentByTag = getSupportFragmentManager()
						.findFragmentByTag(
								makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = "
						+ fragmentByTag);

				return FindPlacesByAutoCompleteTextViewFragment.newInstance(
						position, "Find Places");

			}

			else if (position == 1) {
				Fragment fragmentByTag = getSupportFragmentManager()
						.findFragmentByTag(
								makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = "
						+ fragmentByTag);

				return FindNearbyPlacesFragment.newInstance(position,
						"Find Nearby Places");

			}

			else if (position == 2) {
				Fragment fragmentByTag = getSupportFragmentManager()
						.findFragmentByTag(
								makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = "
						+ fragmentByTag);

				return AnimatingFragment.newInstance(position,
						"Animating Markers");

			}

			else {
				Fragment fragmentByTag = getSupportFragmentManager()
						.findFragmentByTag(
								makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = "
						+ fragmentByTag);

				return TravellingModeFragment.newInstance(position,
						"Driving Mode");
			}
		}

	}

	private static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}

}
