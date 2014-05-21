package com.example.controller;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class PlacesAutoCompleteTextView extends AutoCompleteTextView {

	public PlacesAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	// Returns the Place Description corresponding to the selected item
	
	@SuppressWarnings("unchecked")
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		return hm.get("description");
	}

}
