package com.bclymer.pokecontacts.models;

public class Contact {
	public String name;
	public String photoUri;
	public int hasNumber;
	
	public Contact(String name, String photoUri, int hasNumber) {
		this.name = name;
		this.photoUri = photoUri;
		this.hasNumber = hasNumber;
	}
}
