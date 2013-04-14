package com.bclymer.pokecontacts;

import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bclymer.pokecontacts.database.ContactDAO;
import com.bclymer.pokecontacts.models.Contact;
import com.bclymer.pokecontacts.util.StringUtil;

public class ContactDetailsFragment extends SherlockFragment {
	
	private Context mContext;
	private Contact mContact;
	private ImageView vPhoto;
	private TextView vName;
	private TextView vType;
	private TextView vHeight;
	private TextView vWeight;
	private TextView vId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_contact_details, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		vName = ((TextView) getActivity().findViewById(R.id.contact_details_name));
		vType = ((TextView) getActivity().findViewById(R.id.contact_details_type));
		vHeight = ((TextView) getActivity().findViewById(R.id.contact_details_height));
		vWeight = ((TextView) getActivity().findViewById(R.id.contact_details_weight));
		vId = ((TextView) getActivity().findViewById(R.id.contact_details_id));
		vPhoto = ((ImageView) getActivity().findViewById(R.id.contact_details_photo));
	}
	
	public void setContactId(int contactId, int displayId) {
		mContact = ContactDAO.getContactById(contactId);
		mContact.displayId = displayId;
		Bitmap bmpLarge = null;
		try {
			bmpLarge = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(mContact.photoUri));
			if (bmpLarge == null) {
				bmpLarge = ImageManager.defaultIconFull;
			}
		} catch (Exception e) {
			bmpLarge = ImageManager.defaultIconFull; 
		}
		vPhoto.setImageBitmap(ImageManager.getRoundedCornerBitmap(bmpLarge, 15, 96, 96));
		vName.setText(mContact.name.toUpperCase(Locale.US));
		vType.setText(mContact.type == null ? "TYPE" : mContact.type.toUpperCase(Locale.US));
		vHeight.setText(inchesToFormattedHeight(mContact.height));
		vWeight.setText(mContact.weight + ".0");
		vId.setText("No. " + StringUtil.pad(Integer.toString(mContact.displayId), 3, '0'));
	}
	
	public String inchesToFormattedHeight(int inches) {
		StringBuilder str = new StringBuilder();
		str.append(inches / 12);
		str.append("'");
		str.append(inches % 12);
		str.append("\"");
		return str.toString();
	}
}
