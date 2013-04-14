package com.bclymer.pokecontacts.models;

public class Contact {
	
	public int id;
	public String name;
	public String thumbUri;
	public String photoUri;
	public String number;
	public String description;
	public int height;
	public int weight;
	public String cry;
	public String type;
	public int displayId;
	
	public Contact(int id) {
		this.id = id;
	}
	
	public Contact(int id, String name, String thumbUri, String photoUri, String number) {
		this.id = id;
		this.name = name;
		this.thumbUri = thumbUri;
		this.photoUri = photoUri;
		this.number = number;
	}

	public Contact(int id, String name, String thumbUri, String photoUri, String number, String description, int height, int weight, String cry, String type) {
		this.id = id;
		this.name = name;
		this.thumbUri = thumbUri;
		this.photoUri = photoUri;
		this.number = number;
		this.description = description;
		this.height = height;
		this.weight = weight;
		this.cry = cry;
		this.type = type;
	}
	
	
}
