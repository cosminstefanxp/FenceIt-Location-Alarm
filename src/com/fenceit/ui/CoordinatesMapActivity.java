package com.fenceit.ui;

import android.os.Bundle;

import com.fenceit.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class CoordinatesMapActivity extends MapActivity {

	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location_map);
		mapView = (MapView) findViewById(R.id.coordinatesMap_mapView);

		// The following line should not be required but it is,
		// up through Froyo (Android 2.2)
		mapView.postInvalidateDelayed(2000);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
