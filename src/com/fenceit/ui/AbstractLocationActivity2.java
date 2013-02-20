/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fenceit.R;
import com.fenceit.alarm.locations.AbstractAlarmLocation;

/**
 * The Class AbstractLocationActivity is used as a base for implementing Activities for editing
 * AlarmLocations. It handles the click events on options particular to {@link AbstractAlarmLocation}, such as
 * favorite and name.
 */
public abstract class AbstractLocationActivity2 extends DefaultActivity {

	/** The Constant DIALOG_LOCATION_NAME. */
	private static final int DIALOG_LOCATION_NAME = 999;

	/**
	 * Checks if the instance of the Activity forces the location to be favorite. This is the case, for
	 * example, when the LocationActivity was created from the LocationsPanel, in which case, not forcing it
	 * to be favorite would be useless.
	 */
	protected boolean isForcedFavorite = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		// Check if this
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu options that are common to all location activities
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_location, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Process the menu options that are common to all location activities
		switch (item.getItemId()) {
		case R.id.menu_save:
			saveLocation();
			return true;
		case R.id.menu_undo:
			undoLocation();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void saveLocation() {

	}

	protected void undoLocation() {

	}

	/**
	 * Method triggered when the user clicks on the favorite section.
	 * 
	 * @param v the view
	 */
	public void onClickFavoriteSection(View v) {
		// If the location is forced to be a favorite one
		if (isForcedFavorite) {
			Toast.makeText(this,
					"The location can only be favorite, as it is created from the Locations Panel.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		AbstractAlarmLocation location = getLocation();
		// Change location favorite status
		location.setFavorite(!location.isFavorite());
		// Change displayed elements
		refreshAbstractLocationElements();
	}

	/**
	 * Method triggered when the user clicks on the name section.
	 * 
	 * @param v the view
	 */
	public void onClickNameSection(View v) {
		showDialog(DIALOG_LOCATION_NAME);
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	protected abstract AbstractAlarmLocation getLocation();

	/**
	 * Refreshes the graphical elements corresponding to the abstract location's options (favorite, name).
	 */
	protected void refreshAbstractLocationElements() {
		// Change favorite location image
		if (getLocation().isFavorite())
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_on);
		else
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_off);

		// If the location is not favorite, don't set the name
		if (getLocation().isFavorite()) {
			findViewById(R.id.location_nameSection).setVisibility(View.VISIBLE);
			// Change location name
			((TextView) findViewById(R.id.location_nameText)).setText(getLocation().getName());
		} else
			findViewById(R.id.location_nameSection).setVisibility(View.GONE);
	}

	/**
	 * Creates any abstract location dialog that might be necessary. Returns null if it doesn't handle the
	 * requested Dialog id.
	 * 
	 * @param id the id of the dialog to generate
	 * @return the dialog, or null if the id was not recognized
	 */
	protected Dialog createAbstractLocationDialog(int id) {
		if (!(id == DIALOG_LOCATION_NAME))
			return null;

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_LOCATION_NAME:
			// Create the dialog associated with setting Alarm name
			builder.setTitle("Location name");
			builder.setMessage("Set the location name:");
			// Prepare the text edit, including with margins
			LayoutInflater factory = LayoutInflater.from(this);
			View nameDialogView = factory.inflate(R.layout.dialog_edit_text_layout, null);
			final EditText nameText = (EditText) nameDialogView.findViewById(R.id.dialog_editText);
			nameText.setText(getLocation().getName());
			builder.setView(nameDialogView);

			// Only use an OK button
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getLocation().setName(nameText.getText().toString());
					((TextView) findViewById(R.id.location_nameText)).setText(getLocation().getName());
				}
			});

			// Build the dialog
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

}
