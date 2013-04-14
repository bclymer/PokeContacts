package com.bclymer.pokecontacts;

import com.bclymer.pokecontacts.database.DatabaseHelper;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class PokeApplication extends Application {
	
	private static DatabaseHelper dbHelper;
	private static PokeApplication instance;
	public static ImageManager mImageManager;
	
	@Override
    public void onCreate() {
        super.onCreate();
		instance = this;
		dbHelper = new DatabaseHelper(this);
		mImageManager = new ImageManager(this);
    }
	
	public static PokeApplication getInstance() {
		return instance;
	}
	
	public SQLiteDatabase getReadableDatabase() {
		return dbHelper.getReadableDatabase();
	}

	public SQLiteDatabase getWritableDatabase() {
		synchronized (instance) {
			return dbHelper.getWritableDatabase();
		}
	}
}
