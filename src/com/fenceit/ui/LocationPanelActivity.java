/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fenceit.R;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.ui.adapters.AlarmAdapter;
import com.fenceit.ui.adapters.LocationsAdapter;

/**
 * The Class LocationPanelActivity.
 */
public class LocationPanelActivity extends DefaultActivity  implements OnItemClickListener {

	/** The list view. */
	ListView listView;

	/** The list adapter. */
	LocationsAdapter listAdapter;

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_panel);
		
		//TODO: to fix this...
		//((TextView) findViewById(R.id.title_titleText)).setText("Locations");

		Random rand = new Random();
		ArrayList<AlarmLocation> locations = new ArrayList<AlarmLocation>();
		CoordinatesLocation loc = new CoordinatesLocation();
		loc.setActivationDistance(1000);
		loc.setLatitude(rand.nextDouble() * 6 + 43);
		loc.setLongitude(rand.nextDouble() * 6 + 24);
		loc.setName("Home");
		locations.add(loc);

		WifiConnectedLocation loc1 = new WifiConnectedLocation();
		loc1.setName("Pizza Hut Victoriei");
		loc1.setSsid("RomtelecomWEP 1383");
		loc1.setBssid("7a:93:a0:e2:5e:58");
		locations.add(loc1);

		// Prepare the listview
		listView = (ListView) findViewById(R.id.locationPanel_locationList);
		listAdapter = new LocationsAdapter(this, locations);
		listView.setAdapter(listAdapter);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 * android.view.View, int, long) */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startActivity(new Intent(this, NotificationActivity.class));

	}

}
