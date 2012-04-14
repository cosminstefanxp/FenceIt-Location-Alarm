/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Class DefaultDatabaseHelper.
 *
 * @param <T> the generic type
 */
public class DefaultDatabaseHelper<T> extends SQLiteOpenHelper {

	/** The name of the id field in the object. */
	protected static final String ID_FIELD = "id";

	/** The column names. */
	protected String mColumnNames[];

	/** The table name used for this object. */
	protected final String mTableName;

	/** The class. */
	protected final Class<T> mClass;

	/** The m create query. */
	protected String mCreateQuery = null;

	/** The logger. */
	private static Logger log = Logger.getLogger(DefaultDatabaseHelper.class);

	/**
	 * Checks if the field is transient.
	 * 
	 * @param field the field
	 * @return true, if is transient
	 */
	private boolean isTransient(Field field) {
		Transient annotation = field.getAnnotation(Transient.class);
		if (annotation == null)
			return false;
		else
			return true;
	}

	/**
	 * Instantiates a new default database helper.
	 *
	 * @param context the context
	 * @param c the class
	 * @param tableName the table name
	 */
	public DefaultDatabaseHelper(Context context, Class<T> c, String tableName) {
		super(context, DatabaseDefaults.DATABASE_NAME, null, DatabaseDefaults.DATABASE_VERSION);
		this.mTableName = tableName;
		this.mClass = c;
	}

	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName() {
		return mTableName;
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		// If the createQuery was already generated, use it
		if (mCreateQuery != null)
			database.execSQL(mCreateQuery);

		// Create Create Table Statement
		Field[] fields = mClass.getDeclaredFields();
		String createQuery = "CREATE TABLE " + mTableName + " ( ";
		assert(fields.length>0);

		for (Field field : fields) {
			{
				field.setAccessible(true);
				// if it's a transient field, skip it
				if (isTransient(field))
					continue;

				// Check if it's the id field
				if (field.getName().equals(ID_FIELD)) {
					createQuery += "_id integer primary key autoincrement, ";
					continue;
				}

				// Special type checks
				if (field.getClass().equals(Double.class) || field.getClass().equals(double.class)
						|| field.getClass().equals(Float.class) || field.getClass().equals(float.class))
					createQuery += field.getName() + " real, ";
				else if (field.getClass().equals(Integer.class) || field.getClass().equals(int.class)
						|| field.getClass().equals(Short.class) || field.getClass().equals(short.class)
						|| field.getClass().equals(Byte.class) || field.getClass().equals(byte.class))
					createQuery += field.getName() + " integer, ";
				else
					createQuery += field.getName() + " text not null, ";
			}
		}

		createQuery = createQuery.substring(0, createQuery.length() - 2);
		createQuery += ");";
		
		// Create the table
		database.execSQL(createQuery);
		mCreateQuery = createQuery;
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Delete all existing data and re-create the table
		log.warn("Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all exiting data.");
		db.execSQL("DROP TABLE IF EXISTS " + mTableName);
		onCreate(db);
	}

}
