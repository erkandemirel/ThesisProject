package com.example.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.SupportMapFragment;

public class SherlockMapFragment extends SupportMapFragment implements
		OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener,
		OnOptionsItemSelectedListener {
	private SherlockFragmentActivity sherlockActivity;

	public SherlockFragmentActivity getSherlockActivity() {
		return sherlockActivity;
	}

	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof SherlockFragmentActivity)) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " must be attached to a SherlockFragmentActivity.");
		}
		sherlockActivity = (SherlockFragmentActivity) activity;

		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		sherlockActivity = null;
		super.onDetach();
	}

	@Override
	public final void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater) {
		onCreateOptionsMenu(new MenuWrapper(menu),
				sherlockActivity.getSupportMenuInflater());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	}

	@Override
	public final void onPrepareOptionsMenu(android.view.Menu menu) {
		onPrepareOptionsMenu(new MenuWrapper(menu));
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		return onOptionsItemSelected(new MenuItemWrapper(item));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return false;
	}

	protected void onPostCreate(Bundle savedInstanceState) {

	}
}
