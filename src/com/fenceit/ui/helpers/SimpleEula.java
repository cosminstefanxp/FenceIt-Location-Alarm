/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.helpers;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.fenceit.R;

/**
 * The Class SimpleEula. Adapted from code by Agilevent:
 * https://github.com/Agilevent
 * /Eula-Sample/blob/master/src/com/agilevent/eulasample/SimpleEula.java
 */
public class SimpleEula {

	/** The eula prefix. */
	private String EULA_PREFIX = "eula_";

	/** The m activity. */
	private Activity mActivity;

	/**
	 * Instantiates a new simple eula.
	 * 
	 * @param context the context
	 */
	public SimpleEula(Activity context) {
		mActivity = context;
	}

	/**
	 * Gets the package info.
	 * 
	 * @return the package info
	 */
	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	/**
	 * Show.
	 */
	public void show(boolean forced) {
		PackageInfo versionInfo = getPackageInfo();

		// the eulaKey changes every time you increment the version number in
		// the AndroidManifest.xml
		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
		if (hasBeenShown == false || forced == true) {

			// Show the Eula
			String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;

			// Read the contents of our asset
			InputStream stream;
			String content = "";
			try {
				stream = mActivity.getAssets().open("eula.txt");
				int size = stream.available();
				byte[] buffer = new byte[size];
				stream.read(buffer);
				stream.close();
				content = new String(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Includes the updates as well so users know what changed.
			// String message = mActivity.getString(R.string.updates) + "\n\n"
			// + mActivity.getString(R.string.eula);
			String message = content;

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(title)
					.setMessage(message).setPositiveButton("I agree", new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							// Mark this version as read.
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(eulaKey, true);
							editor.commit();
							dialogInterface.dismiss();
						}
					}).setNegativeButton("I disagree", new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Close the activity, as they have declined the
							// EULA
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(eulaKey, false);
							editor.commit();
							mActivity.finish();
						}

					});
			builder.create().show();
		}
	}
}
