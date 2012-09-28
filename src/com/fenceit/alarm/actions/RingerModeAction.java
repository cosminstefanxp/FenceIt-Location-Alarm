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

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#execute(android.content.Context)
	 */
	@Override
	public void execute(Context context) {
		log.warn("Change Ringer Mode action triggered: " + getDescription());
		AudioManager audio_mngr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audio_mngr.setRingerMode(targetRingerMode);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#isComplete()
	 */
	@Override
	public boolean isComplete() {
		return targetRingerMode == AudioManager.RINGER_MODE_NORMAL
				|| targetRingerMode == AudioManager.RINGER_MODE_VIBRATE
				|| targetRingerMode == AudioManager.RINGER_MODE_SILENT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#getDescription()
	 */
	@Override
	public String getDescription() {
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

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return "Change Ringer Mode Action";
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#getType()
	 */
	@Override
	public ActionType getType() {
		return ActionType.RingerModeAction;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.actions.AlarmAction#getTypeImageResource()
	 */
	@Override
	public int getTypeImageResource() {
		return android.R.drawable.ic_lock_silent_mode_off;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RingerModeAction [" + getDescription() + "]";
	}

	public int getTargetRingerMode() {
		return targetRingerMode;
	}

	public void setTargetRingerMode(int targetRingerMode) {
		this.targetRingerMode = targetRingerMode;
	}
	

}
