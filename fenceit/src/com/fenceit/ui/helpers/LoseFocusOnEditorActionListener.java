package com.fenceit.ui.helpers;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoseFocusOnEditorActionListener implements OnEditorActionListener {
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			v.setFocusable(false);
			v.setFocusableInTouchMode(true);
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		Log.d("com.fenceit", actionId + "/" + event);
		return false;
	}
}
