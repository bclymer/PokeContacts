package com.bclymer.pokecontacts.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bclymer.pokecontacts.PokeApplication;
import com.bclymer.pokecontacts.models.Contact;

public class ContactDAO {
	
	private static String[] allColumns = {
		DatabaseHelper.CONTACTS_ID,
		DatabaseHelper.CONTACTS_CRY,
		DatabaseHelper.CONTACTS_DESCRIPTION,
		DatabaseHelper.CONTACTS_HEIGHT,
		DatabaseHelper.CONTACTS_NAME,
		DatabaseHelper.CONTACTS_NUMBER,
		DatabaseHelper.CONTACTS_PHOTO_URI,
		DatabaseHelper.CONTACTS_THUMB_URI,
		DatabaseHelper.CONTACTS_WEIGHT,
		DatabaseHelper.CONTACTS_TYPE
	};
	
	private static String[] namePhotoNumber = {
		DatabaseHelper.CONTACTS_ID,
		DatabaseHelper.CONTACTS_PHOTO_URI,
		DatabaseHelper.CONTACTS_NAME,
		DatabaseHelper.CONTACTS_NUMBER
	};
	
	private static String[] justId = {
		DatabaseHelper.CONTACTS_ID
	};
	
	public static List<Contact> getAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		SQLiteDatabase db = PokeApplication.getInstance().getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.TABLE_CONTACTS, namePhotoNumber, null, null, null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			Contact contact = cursorToMinimalContact(c);
			contacts.add(contact);
			c.moveToNext();
		}
		c.close();
		db.close();
		return contacts;
	}
	
	public static List<Contact> getAllContactsJustId() {
		List<Contact> contacts = new ArrayList<Contact>();
		SQLiteDatabase db = PokeApplication.getInstance().getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.TABLE_CONTACTS, justId, null, null, null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			Contact contact = cursorToJustIdContact(c);
			contacts.add(contact);
			c.moveToNext();
		}
		c.close();
		db.close();
		return contacts;
	}
	
	public static Contact getContactById(int contactId) {
		SQLiteDatabase db = PokeApplication.getInstance().getReadableDatabase();
		Cursor c = db.query(DatabaseHelper.TABLE_CONTACTS, allColumns, DatabaseHelper.CONTACTS_ID + " = ?", new String[] { Integer.toString(contactId) }, null, null, null);
		c.moveToFirst();
		Contact contact = null;
		if (!c.isAfterLast()) {
			contact = cursorToFullContact(c);
		}
		c.close();
		db.close();
		return contact;
	}
	
	public static void updateMinimalContact(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.CONTACTS_NAME, contact.name);
		values.put(DatabaseHelper.CONTACTS_NUMBER, contact.number);
		values.put(DatabaseHelper.CONTACTS_PHOTO_URI, contact.photoUri);
		SQLiteDatabase db = PokeApplication.getInstance().getWritableDatabase();
		db.update(DatabaseHelper.TABLE_CONTACTS, values, DatabaseHelper.CONTACTS_ID + " = ?", new String[] { Integer.toString(contact.id) });
		db.close();
	}
	
	public static void updateFullContact(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.CONTACTS_CRY, contact.cry);
		values.put(DatabaseHelper.CONTACTS_DESCRIPTION, contact.description);
		values.put(DatabaseHelper.CONTACTS_HEIGHT, contact.height);
		values.put(DatabaseHelper.CONTACTS_WEIGHT, contact.weight);
		values.put(DatabaseHelper.CONTACTS_TYPE, contact.type);
		SQLiteDatabase db = PokeApplication.getInstance().getWritableDatabase();
		db.update(DatabaseHelper.TABLE_CONTACTS, values, DatabaseHelper.CONTACTS_ID + " = ?", new String[] { Integer.toString(contact.id) });
		db.close();
	}
	
	/**
	 * Add new contacts, update existing, and delete old.
	 * @param oldContacts If null, all contact ids will be loaded from db.
	 * @param newContacts New contacts you want to add.
	 */
	public static void createContacts(List<Contact> oldContacts, List<Contact> newContacts) {
		if (oldContacts == null) {
			oldContacts = getAllContactsJustId();
		}
		ArrayList<Contact> toCreate = new ArrayList<Contact>();
		ArrayList<Contact> toDelete = new ArrayList<Contact>();
		ArrayList<Contact> toUpdate = new ArrayList<Contact>();
		for (Contact newContact : newContacts) {
			boolean found = false;
			for (Contact oldContact : oldContacts) {
				if (oldContact.id == newContact.id) {
					toUpdate.add(newContact);
					oldContacts.remove(oldContact);
					found = true;
					break;
				}
			}
			if (!found) {
				toCreate.add(newContact);
			}
		}
		for (Contact oldContact : oldContacts){
			toDelete.add(oldContact);
		}
		if (toCreate.size() > 0 || toDelete.size() > 0 || toUpdate.size() > 0) {
			SQLiteDatabase db = PokeApplication.getInstance().getWritableDatabase();
			db.beginTransaction();
			for (Contact contact : toCreate) {
				ContentValues values = new ContentValues();
				values.put(DatabaseHelper.CONTACTS_ID, contact.id);
				values.put(DatabaseHelper.CONTACTS_NAME, contact.name);
				values.put(DatabaseHelper.CONTACTS_NUMBER, contact.number);
				values.put(DatabaseHelper.CONTACTS_PHOTO_URI, contact.photoUri);
				values.put(DatabaseHelper.CONTACTS_THUMB_URI, contact.thumbUri);
				db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
			}
			Log.i("Poke DB", "Added " + toCreate.size() + " contacts");
			for (Contact contact : toUpdate) {
				ContentValues values = new ContentValues();
				values.put(DatabaseHelper.CONTACTS_NAME, contact.name);
				values.put(DatabaseHelper.CONTACTS_NUMBER, contact.number);
				values.put(DatabaseHelper.CONTACTS_PHOTO_URI, contact.photoUri);
				values.put(DatabaseHelper.CONTACTS_THUMB_URI, contact.thumbUri);
				db.update(DatabaseHelper.TABLE_CONTACTS, values, DatabaseHelper.CONTACTS_ID + " = ?", new String[] { Integer.toString(contact.id) });
			}
			Log.i("Poke DB", "Updated " + toUpdate.size() + " contacts");
			for (Contact contact : toDelete) {
				db.delete(DatabaseHelper.TABLE_CONTACTS, DatabaseHelper.CONTACTS_ID + " = ?", new String[] { Integer.toString(contact.id) });
			}
			Log.i("Poke DB", "Deleted " + toDelete.size() + " contacts");
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
	}

	private static Contact cursorToMinimalContact(Cursor c) {
		int id = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACTS_ID));
		String name = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_NAME));
		String photoUri = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_PHOTO_URI));
		String thumbUri = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_THUMB_URI));
		String number = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_NUMBER));
		return new Contact(id, name, thumbUri, photoUri, number);
	}

	private static Contact cursorToJustIdContact(Cursor c) {
		int id = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACTS_ID));
		return new Contact(id);
	}

	private static Contact cursorToFullContact(Cursor c) {
		int id = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACTS_ID));
		String name = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_NAME));
		String photoUri = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_PHOTO_URI));
		String thumbUri = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_THUMB_URI));
		String number = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_NUMBER));
		String description = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_DESCRIPTION));
		int height = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACTS_HEIGHT));
		int weight = c.getInt(c.getColumnIndex(DatabaseHelper.CONTACTS_WEIGHT));
		String cry = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_CRY));
		String type = c.getString(c.getColumnIndex(DatabaseHelper.CONTACTS_TYPE));
		return new Contact(id, name, thumbUri, photoUri, number, description, height, weight, cry, type);
	}
}
