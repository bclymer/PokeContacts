package com.bclymer.pokecontacts.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;
	public static final String DB_NAME = "pokecontacts.db";
	public final static String TABLE_CONTACTS = "contacts";
	public final static String CONTACTS_ID = "_id";
	public final static String CONTACTS_NAME = "name";
	public final static String CONTACTS_NUMBER = "number";
	public final static String CONTACTS_HEIGHT = "height";
	public final static String CONTACTS_WEIGHT = "weight";
	public final static String CONTACTS_DESCRIPTION = "description";
	public final static String CONTACTS_CRY = "cry";
	public final static String CONTACTS_PHOTO_URI = "photo_uri";
	public final static String CONTACTS_THUMB_URI = "thumb_uri";
	public final static String CONTACTS_TYPE = "type";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" +
				CONTACTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				CONTACTS_NAME + " VARCHAR(255), " +
				CONTACTS_NUMBER + " VARCHAR(255), " +
				CONTACTS_PHOTO_URI + " VARCHAR(255), " +
				CONTACTS_THUMB_URI + " VARCHAR(255), " +
				CONTACTS_TYPE + " VARCHAR(255), " +
				CONTACTS_HEIGHT + " INTEGER, " +
				CONTACTS_WEIGHT + " INTEGER, " +
				CONTACTS_DESCRIPTION + " VARCHAR(255), " +
				CONTACTS_CRY + " VARCHAR(255));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
