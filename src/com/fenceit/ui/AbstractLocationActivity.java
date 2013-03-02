/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
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
public abstract class AbstractLocationActivity<T extends AbstractAlarmLocation> extends DefaultActivity {

	protected Logger log = Logger.getLogger(AbstractLocationActivity.class);

	/** The Constant DIALOG_LOCATION_NAME. */
	private static final String DIALOG_LOCATION_NAME = "location_name";

	/**
	 * Checks if the instance of the Activity forces the location to be favorite. This is the case, for
	 * example, when the LocationActivity was created from the LocationsPanel, in which case, not forcing it
	 * to be favorite would be useless.
	 */
	protected boolean isForcedFavorite = false;

	/** The location. */
	protected T location;

	/** If it's a new entity. */
	private boolean newEntity;

	/** The original location. Used for undoing. */
	private T originalLocation;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the location from the database
			Bundle extras = getIntent().getExtras();
			Long locationID = (Long) (extras != null ? extras.get("id") : null);
			// See if the location is forced to be favorite
			if (extras != null)
				isForcedFavorite = extras.getBoolean("forced");

			fetchLocation(locationID);
			originalLocation = (T) location.copy();
		}
		// If it's a restored instance
		else {
			// See if the location is forced to be favorite
			isForcedFavorite = savedInstanceState.getBoolean("forced");

			// Get the unsaved location from the saved instance
			location = (T) savedInstanceState.getSerializable("location");
			if (log.isInfoEnabled()) {
				log.info("Restored saved instance of location: " + location);
			}
			originalLocation = (T) savedInstanceState.getSerializable("original_location");
		}
		postFetchLocation();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("location", this.location);
		outState.putBoolean("forced", this.isForcedFavorite);
		outState.putSerializable("original_location", this.originalLocation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu options that are common to all location activities
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_location, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Process the menu options that are common to all location activities
		switch (item.getItemId()) {
		case R.id.menu_save:
			log.info("Save button clicked. Storing entity...");
			if (!saveLocation()) {
				Toast.makeText(this, R.string.location_save_incomplete_fields, Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = new Intent();
			intent.putExtra("id", location.getId());
			intent.putExtra("type", location.getType().toString());
			setResult(RESULT_OK, intent);
			finish();
			return true;
		case R.id.menu_undo:
			undoLocation();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected boolean saveLocation() {
		// Checks
		if (location == null) {
			log.error("No location to store in database.");
			return false;
		}

		preStoreLocation();

		// Check if all data is all right
		if (!location.isComplete()) {
			log.error("Not all required fields are filled in:" + location);
			return false;
		}

		// Save the entity to the database
		log.debug("Saving location in database...");
		DefaultDAO<T> dao = getDAO();
		dao.open();
		if (newEntity) {
			long id = dao.insert(location, true);
			if (id == -1)
				return false;
			log.info("Successfully saved new location with id: " + id);
			location.setId(id);
			newEntity = false;
		} else
			dao.update(location, location.getId());
		dao.close();

		return true;
	}

	@SuppressWarnings("unchecked")
	protected void undoLocation() {
		this.location = (T) this.originalLocation.copy();
		postFetchLocation();
		this.refreshLocationView();
		this.refreshAbstractLocationView();
		Toast.makeText(this, R.string.undone_success, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Fetches the associated location from the database, or builds a new one, if no id was provided.
	 * 
	 * @param locationID the location id
	 */
	protected void fetchLocation(Long locationID) {
		// Fetch from database
		if (locationID != null) {
			log.info("Fetching location from database with id: " + locationID);
			DefaultDAO<T> dao = getDAO();
			dao.open();
			location = dao.fetch(locationID);
			dao.close();
			if (log.isDebugEnabled())
				log.debug("Fetched location: " + location);
			if (location != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new " + this.getClass().getSimpleName());
		location = instantiateLocation();
		newEntity = true;
		if (isForcedFavorite)
			location.setFavorite(true);
	}

	protected abstract DefaultDAO<T> getDAO();

	protected abstract T instantiateLocation();

	protected abstract void refreshLocationView();

	protected abstract void postFetchLocation();

	protected abstract void preStoreLocation();

	/**
	 * Method triggered when the user clicks on the favorite section.
	 * 
	 * @param v the view
	 */
	public void onClickFavoriteSection(View v) {
		// If the location is forced to be a favorite one
		if (isForcedFavorite) {
			Toast.makeText(this, R.string.location_favorite_only, Toast.LENGTH_SHORT).show();
			return;
		}

		// Change location favorite status
		location.setFavorite(!location.isFavorite());
		// Change displayed elements
		refreshAbstractLocationView();
	}

	/**
	 * Method triggered when the user clicks on the name section.
	 * 
	 * @param v the view
	 */
	public void onClickNameSection(View v) {
		LocationNameDialogFragment dialog = new LocationNameDialogFragment();
		dialog.show(this.getSupportFragmentManager(), DIALOG_LOCATION_NAME);
	}

	/**
	 * Refreshes the graphical elements corresponding to the abstract location's options (favorite, name).
	 */
	protected void refreshAbstractLocationView() {
		// Change favorite location image
		if (this.location.isFavorite())
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_on);
		else
			((ImageView) findViewById(R.id.location_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_off);

		// If the location is not favorite, don't set the name
		if (this.location.isFavorite()) {
			findViewById(R.id.location_nameSection).setVisibility(View.VISIBLE);
			// Change location name
			((TextView) findViewById(R.id.location_nameText)).setText(location.getDisplayName(this));
		} else
			findViewById(R.id.location_nameSection).setVisibility(View.GONE);
	}

	@SuppressLint("ValidFragment")
	public class LocationNameDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// Create the dialog associated with setting Alarm name
			builder.setTitle(R.string.dialog_location_name_title);
			// Prepare the text edit, including with margins
			LayoutInflater factory = getActivity().getLayoutInflater();
			View nameDialogView = factory.inflate(R.layout.dialog_edit_text_layout, null);
			final EditText nameText = (EditText) nameDialogView.findViewById(R.id.dialog_editText);
			nameText.setText(location.getName());
			nameText.setSelection(nameText.getText().length());
			builder.setView(nameDialogView);

			// Only use an OK button
			builder.setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					location.setName(nameText.getText().toString());
					// Change location name
					((TextView) findViewById(R.id.location_nameText)).setText(location
							.getDisplayName(AbstractLocationActivity.this));
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

}
