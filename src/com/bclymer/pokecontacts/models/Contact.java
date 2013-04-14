package com.bclymer.pokecontacts.models;

import android.net.Uri;

public class Contact {
	
	public long id;
	public String name;
	public Uri photoUri;
	public String number;
	public String ringtone;
	public String email;
	public String note;
	public int displayId;
	
	public Contact(long id, String name, Uri photoUri, String number, String ringtone, String email, String note) {
		this.id = id;
		this.name = name;
		this.photoUri = photoUri;
		this.number = number;
		this.ringtone = ringtone;
		this.email = email;
		this.note = note;
	}
	
}
