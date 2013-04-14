package com.bclymer.pokecontacts;

import java.util.ArrayList;

import android.app.Application;

import com.bclymer.pokecontacts.models.Contact;

public class PokeApplication extends Application {
	
	private static PokeApplication instance;
	public static ImageManager mImageManager;
	public ArrayList<Contact> mContacts;
	
	@Override
    public void onCreate() {
        super.onCreate();
		instance = this;
		mImageManager = new ImageManager(this);
		mContacts = new ArrayList<Contact>();
    }
	
	public static PokeApplication getInstance() {
		return instance;
	}
}
