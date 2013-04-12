package com.fenceit.ui.adapters;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class LocationMapInfoWindowAdapter implements InfoWindowAdapter {

	@RootContext
	Context context;

	@Override
	public View getInfoContents(Marker marker) {
		LocationMapInfoWindowView view = LocationMapInfoWindowView_.build(context);

		// Check to see if there is an optional text embedded with the
		// description
		String description = marker.getSnippet();
		String optional = null;
		int index = description.lastIndexOf('~');
		if (index != -1) {
			optional = description.substring(index + 1);
			description = description.substring(0, index);
		}

		view.bind(marker.getTitle(), description, optional);
		return view;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}
