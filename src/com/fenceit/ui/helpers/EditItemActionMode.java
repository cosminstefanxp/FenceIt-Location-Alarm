/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.helpers;

import android.util.Log;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fenceit.R;

/**
 * The Class EditItemActionMode used to implement a default Action Mode Callback
 * used for generating an action mode for items.
 */
public abstract class EditItemActionMode implements Callback {

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.menu_edit_item, menu);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		Log.d("d", "aici");
		switch (item.getItemId()) {
		case R.id.menu_edit_item:
			onEditItem(mode);
			break;
		case R.id.menu_delete_item:
			onDeleteItem(mode);
			break;
		}
		return false;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	/**
	 * Edits the item(s) to which this action mode applies.
	 *
	 * @param mode the mode
	 */
	protected abstract void onEditItem(ActionMode mode);

	/**
	 * Deletes the item(s) to which this action mode applies.
	 *
	 * @param mode the mode
	 */
	protected abstract void onDeleteItem(ActionMode mode);

}
