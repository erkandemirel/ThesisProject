package com.example.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.astuetz.PagerSlidingTabStrip;

import fragments.BookmarksFragment;
import fragments.FindNearbyPlacesFragment;
import fragments.TravellingModeFragment;

public class TabActivity extends SherlockFragmentActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private TabPagerAdapter adapter;

	public static DisplayMetrics displayMetrics;

	public static Context mainContext;

	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tabs);

		mainContext = getBaseContext();

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new TabPagerAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);

		// Creating an instance of DisplayMetrics
		displayMetrics = new DisplayMetrics();

		// Getting the screen display metrics
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
	}

	public class TabPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Nearby Places",
				"Travelling Mode", "Bookmarks" };

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
						"Find Nearby Places");

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

}