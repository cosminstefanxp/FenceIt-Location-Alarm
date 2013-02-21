/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.helpers;

import com.fenceit.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * The ErrorDialogFragment which can be used to show an error message to the user.
 */
public class ErrorDialogFragment extends DialogFragment {

	/**
	 * New instance.
	 * 
	 * @param title the title
	 * @param message the message
	 * @return the error dialog fragment
	 */
	public static ErrorDialogFragment newInstance(String title, String message) {
		ErrorDialogFragment f = new ErrorDialogFragment();

		// Supply inputs as arguments.
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		f.setArguments(args);

		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Screen rotation bug fix
		setRetainInstance(true);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(getArguments().getString("title"));
		builder.setMessage(getArguments().getString("message")).setCancelable(false);
		builder.setNeutralButton(R.string.general_ok, null);

		// Create the AlertDialog object and return it
		return builder.create();
	}
}