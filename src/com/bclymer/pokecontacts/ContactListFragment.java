package com.bclymer.pokecontacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bclymer.pokecontacts.models.Contact;
import com.bclymer.pokecontacts.util.StringUtil;

public class ContactListFragment extends SherlockFragment implements OnItemClickListener {

	private static final int DATA = 0;
	private static final int CRY = 1;
	private static final int AREA = 2;
	private static final int CALL = 3;
	private static final int TEXT = 4;
	private Context mContext;
	private List<Contact> mContacts;
	private ListView mContactsListView;
	private ListView mOptionsListView;
	private ContactsAdapter mAdapter;
	private TextView vContactsCount;
	private int selectedIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();

		mContactsListView = (ListView) getActivity().findViewById(R.id.contacts_fragment_listview);
		mContactsListView.setDivider(null);
		mContactsListView.setOnItemClickListener(this);

		mOptionsListView = (ListView) getActivity().findViewById(R.id.contacts_fragment_options_listview);
		mOptionsListView.setDivider(null);
		mOptionsListView.setOnItemClickListener(this);
		String[] options = new String[] { "DATA", "CRY", "AREA", "CALL", "TEXT" };
		ArrayList<String> optionsList = new ArrayList<String>(Arrays.asList(options));
		ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(mContext, R.layout.simple_row, optionsList);
		mOptionsListView.setAdapter(optionsAdapter);

		vContactsCount = (TextView) getActivity().findViewById(R.id.contacts_fragment_contact_count);
		
		Cursor contactCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		mContacts = PokeApplication.getInstance().mContacts;
		mContacts.clear();
		while (contactCursor.moveToNext()) {
			int id = contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			boolean dup = false;
			for (Contact contact : mContacts) {
				if (contact.id == id) {
					dup = true;
					break;
				}
			}
			if (dup) {
				continue;
			}
			String ringtone = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
			String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			long photoId = contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_ID));
			Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
			String phoneNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String email = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
			String note = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
			mContacts.add(new Contact(id, name, photoUri, phoneNumber, ringtone, email, note));
		}
		contactCursor.close();
		Collections.sort(mContacts, new Comparator<Contact>() {
			@Override
			public int compare(Contact c1, Contact c2) {
				return c1.name.compareTo(c2.name);
			}
		});
		vContactsCount.setText(Integer.toString(mContacts.size()));
		mAdapter = new ContactsAdapter();
		mContactsListView.setAdapter(mAdapter);
	}

	private class ContactsAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public ContactsAdapter() {
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return mContacts.size();
		}

		@Override
		public Contact getItem(int position) {
			return mContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.contacts_row, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.contacts_row_name);
				holder.id = (TextView) convertView.findViewById(R.id.contacts_row_id);
				holder.pokeball = (ImageView) convertView.findViewById(R.id.contacts_row_pokeball);
				holder.arrow = (ImageView) convertView.findViewById(R.id.contacts_row_indicator_arrow);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(mContacts.get(position).name.toUpperCase(Locale.US));
			holder.id.setText(StringUtil.pad(Integer.toString(position + 1), 3, '0'));
			if (mContacts.get(position).number != null) {
				holder.pokeball.setVisibility(View.VISIBLE);
			} else {
				holder.pokeball.setVisibility(View.INVISIBLE);
			}
			if (position == selectedIndex) {
				holder.arrow.setVisibility(View.VISIBLE);
			} else {
				holder.arrow.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView name;
		TextView id;
		ImageView pokeball;
		ImageView arrow;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		if (listView == mContactsListView) {
			int start = mContactsListView.getFirstVisiblePosition();
			if (selectedIndex - start >= 0 && selectedIndex <= mContactsListView.getLastVisiblePosition()) {
				mContactsListView
						.getChildAt(selectedIndex - start)
						.findViewById(R.id.contacts_row_indicator_arrow)
						.setVisibility(View.INVISIBLE);
			}
			selectedIndex = position;
			mContactsListView
					.getChildAt(selectedIndex - start)
					.findViewById(R.id.contacts_row_indicator_arrow)
					.setVisibility(View.VISIBLE);
		} else if (listView == mOptionsListView) {
			switch (position) {
			case DATA:
				((ContactListFragmentCallback) getActivity()).showContact(selectedIndex, selectedIndex + 1);
				break;
			case CRY:
				startActivity(new Intent(mContext, CryActivity.class).putExtra("contactId", selectedIndex));
				break;
			case AREA:

				break;
			case CALL:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mContacts.get(selectedIndex).number)));
				break;
			case TEXT:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mContacts.get(selectedIndex).number)));
				break;
			}
		}
	}

	public interface ContactListFragmentCallback {
		public void showContact(int contactId, int displayId);
	}

}
