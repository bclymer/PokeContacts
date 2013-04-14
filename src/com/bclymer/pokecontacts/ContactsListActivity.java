package com.bclymer.pokecontacts;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.bclymer.pokecontacts.ContactListFragment.ContactListFragmentCallback;

public class ContactsListActivity extends SherlockFragmentActivity implements ContactListFragmentCallback {

	private ContactListFragment mContactListFragment;
	private ContactDetailsFragment mContactDetailsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.activity_contacts_list);
		mContactListFragment = (ContactListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contacts_list);
		mContactDetailsFragment = (ContactDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contacts_details);
		
		showContactsList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onBackPressed() {
		if (mContactDetailsFragment.isVisible()) {
			showContactsList();
		} else {
			super.onBackPressed();
		}
	}
	
	public void showContactsList() {
		getSupportFragmentManager().beginTransaction()
			.show(mContactListFragment)
			.hide(mContactDetailsFragment)
			.commit();
	}
	
	public void showContactDetails() {
		getSupportFragmentManager().beginTransaction()
			.show(mContactDetailsFragment)
			.hide(mContactListFragment)
			.commit();
	}

	@Override
	public void showContact(int contactId, int displayId) {
		mContactDetailsFragment.setContactId(contactId, displayId);
		showContactDetails();
	}
}
