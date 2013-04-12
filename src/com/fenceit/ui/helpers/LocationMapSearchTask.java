package com.fenceit.ui.helpers;

import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.fenceit.ui.CoordinatesMapActivity;
import com.google.android.gms.maps.model.LatLng;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class LocationMapSearchTask {

	@RootContext
	CoordinatesMapActivity activity;

	@Background
	public void searchInBackground(String toSearch) {
		try {
			Geocoder geocoder = new Geocoder(activity, Locale.UK);
			List<Address> results = geocoder.getFromLocationName(toSearch, 1);

			if (results.size() == 0) {
				return;
			}

			Address address = results.get(0);
			Log.i("", "Result: " + address);

			updateUI(address);
		} catch (Exception e) {
			Log.e("", "Something went wrong: ", e);
		}
	}
 
	// Notice that we manipulate the activity ref only from the UI thread
	@UiThread
	void updateUI(Address result) {
		activity.showSearchResult(result);
	}
}
