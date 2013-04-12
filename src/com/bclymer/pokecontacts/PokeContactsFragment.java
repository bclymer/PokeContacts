package com.bclymer.pokecontacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bclymer.pokecontacts.models.Contact;

public class PokeContactsFragment extends SherlockFragment implements OnItemClickListener {

	private Context mContext;
	private List<Contact> mContacts;
	private ListView mListView;
	private ContactsAdapter mAdapter;
	private ImageManager mImageManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mListView = (ListView) getActivity().findViewById(R.id.contacts_fragment_listview);
		mListView.setOnItemClickListener(this);
		mImageManager = new ImageManager(mContext);
		mListView.setOnScrollListener(mImageManager);
	}

	@Override
	public void onStart() {
		super.onStart();
		Cursor contactCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		mContacts = new ArrayList<Contact>(contactCursor.getCount());
		while (contactCursor.moveToNext()) {
			String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String photoUri = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
			int hasPhneNumber = contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
			mContacts.add(new Contact(name, photoUri, hasPhneNumber));
		}
		contactCursor.close();
		Collections.sort(mContacts, new Comparator<Contact>() {
			@Override
			public int compare(Contact c1, Contact c2) {
				return c1.name.compareTo(c2.name);
			}
		});
		mAdapter = new ContactsAdapter();
		mListView.setAdapter(mAdapter);
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
				holder.number = (TextView) convertView.findViewById(R.id.contacts_row_number);
				holder.contactIcon = (ImageView) convertView.findViewById(R.id.contacts_row_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(mContacts.get(position).name);
			holder.number.setText(Integer.toString(position));
			if (mContacts.get(position).photoUri != null) {
				mImageManager.display(mContacts.get(position).photoUri, holder.contactIcon);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView name;
		TextView number;
		ImageView contactIcon;
	}

	static class HeaderViewHolder {
		TextView headerText;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

}
