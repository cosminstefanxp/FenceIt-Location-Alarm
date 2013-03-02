/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;
import org.apache.log4j.Logger;

import android.content.Context;
import android.media.AudioManager;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;

/**
 * The Class RingerModeAction.
 */
@DatabaseClass
public class RingerModeAction extends AbstractAlarmAction {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1486486315419529215L;

	/** The Constant tableName. */
	public static final String tableName = "ringer_mode_actions";

	/** The logger. */
	private static Logger log = Logger.getLogger(RingerModeAction.class);

	/** The target ringer mode. */
	@DatabaseField
	private int targetRingerMode = AudioManager.RINGER_MODE_NORMAL;

	/**
	 * Instantiates a new ringer mode action.
	 */
	public RingerModeAction() {
		super(null);
	}

	/**
	 * Instantiates a new ringer mode action.
	 * 
	 * @param alarm the alarm
	 */
	public RingerModeAction(Alarm alarm) {
		super(alarm);
	}

	@Override
	public void execute(Context context) {
		if (log.isInfoEnabled())
			log.info("Change Ringer Mode action triggered: " + getDescription(context));
		AudioManager audio_mngr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audio_mngr.setRingerMode(targetRingerMode);
	}

	@Override
	public boolean isComplete() {
		return targetRingerMode == AudioManager.RINGER_MODE_NORMAL
				|| targetRingerMode == AudioManager.RINGER_MODE_VIBRATE
				|| targetRingerMode == AudioManager.RINGER_MODE_SILENT;
	}

	@Override
	public String getDescription(Context context) {
		String desc;
		switch (targetRingerMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			desc = "Normal";
			break;
		case AudioManager.RINGER_MODE_SILENT:
			desc = "Silent";
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			desc = "Vibrate";
			break;
		default:
			desc = "-Not Set-";
			break;
		}
		return "Set Ringer Mode to " + desc;
	}

	@Override
	public String getTypeDescription(Context context) {
		return "Change Ringer Mode Action";
	}

	@Override
	public ActionType getType() {
		return ActionType.RingerModeAction;
	}

	@Override
	public int getTypeImageResource() {
		return R.drawable.ic_action_ringer_mode;
	}

	@Override
	public String toString() {
		return "RingerModeAction [" + targetRingerMode + "]";
	}

	public int getTargetRingerMode() {
		return targetRingerMode;
	}

	public void setTargetRingerMode(int targetRingerMode) {
		this.targetRingerMode = targetRingerMode;
	}

}
