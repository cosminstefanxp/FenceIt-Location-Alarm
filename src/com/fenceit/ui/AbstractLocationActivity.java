/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.view.View;
import android.widget.ImageView;

import com.fenceit.R;
import com.fenceit.alarm.locations.AbstractAlarmLocation;

/**
 * The Class AbstractLocationActivity is used as a base for implementing Activities for editing
 * AlarmLocations. It handles the click events on options particular to
 * {@link AbstractAlarmLocation}, such as favorite and name.
 */
public abstract class AbstractLocationActivity extends DefaultActivity {

	/**
	 * Method triggered when the user clicks on the favorite section.
	 * 
	 * @param v the view
	 */
	public void onClickFavoriteSection(View v) {
		AbstractAlarmLocation location = getLocation();
		// Change location favorite status
		location.setFavorite(!location.isFavorite());
		// Change image
		if (location.isFavorite())
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_on);
		else
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_off);
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	protected abstract AbstractAlarmLocation getLocation();

	/**
	 * Refreshes the graphical elements corresponding to the abstract location's options (favorite,
	 * name).
	 */
	protected void refreshAbstractLocationElements() {
		// Change favorite location image
		if (getLocation().isFavorite())
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_on);
		else
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_off);
	}
}
