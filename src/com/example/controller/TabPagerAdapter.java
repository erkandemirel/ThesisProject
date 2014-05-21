package com.example.controller;

import com.example.view.FindNearbyPlacesFragment;
import com.example.view.TravellingModeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "Nearby Places", "Travelling Mode"};

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

		return null;

	}

}