package com.bclymer.pokecontacts;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bclymer.pokecontacts.models.Contact;

public class CryActivity extends Activity {

	private ImageButton vPlay;
	private ImageButton vRecord;
	private Button vApplyRingtone;
	private Button vResetRingtone;
	private Contact mContact;
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private boolean isPlaying = false;
	private boolean hasSaved = false;
	private String mFileName;
	private String mCurrentCryPath;
	private Bitmap bStop;
	private Bitmap bPlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cry);

		vPlay = (ImageButton) findViewById(R.id.cry_play);
		vRecord = (ImageButton) findViewById(R.id.cry_record);
		vApplyRingtone = (Button) findViewById(R.id.cry_set_ringtone);
		vResetRingtone = (Button) findViewById(R.id.cry_set_ringtone_default);

		int contactPosition = getIntent().getIntExtra("contactId", -1);
		if (contactPosition == -1) {
			Toast.makeText(this, "Error Loading Cry Data", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		mContact = PokeApplication.getInstance().mContacts.get(contactPosition);
		setTitle(mContact.name + "'s Cry");
		mFileName = getExternalFilesDir(null).getAbsolutePath();
		mFileName += "/" + mContact.id + "-" + System.currentTimeMillis() + ".3gp";
		
		bStop = BitmapFactory.decodeResource(getResources(), R.drawable.stop);
		bPlay = BitmapFactory.decodeResource(getResources(), R.drawable.play);
		
		if (mContact.ringtone == null) {
			vPlay.setEnabled(false);
		} else {
			mCurrentCryPath = mContact.ringtone;
		}

		vPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isPlaying) {
					startPlaying();
					vPlay.setImageBitmap(bPlay);
				} else {
					stopPlaying();
					vPlay.setImageBitmap(bStop);
				}
				isPlaying = !isPlaying;
			}
		});
		vRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmRecording();
			}
		});
		vApplyRingtone.setEnabled(false);
		vApplyRingtone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri localUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, Long.toString(mContact.id));
				ContentValues localContentValues = new ContentValues();
			    localContentValues.put(ContactsContract.Data.CUSTOM_RINGTONE, mFileName);
			    int rowsUpdated = getContentResolver().update(localUri, localContentValues, null, null);
			    Log.e("", "" + rowsUpdated);
			    if (rowsUpdated > 0) {
			    	Toast.makeText(getApplicationContext(), "Ringtone set successfully", Toast.LENGTH_SHORT).show();
			    	hasSaved = true;
			    }
			}
		});
		vResetRingtone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri localUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, Long.toString(mContact.id));
				ContentValues localContentValues = new ContentValues();
			    localContentValues.put(ContactsContract.Data.CUSTOM_RINGTONE, false);
			    int rowsUpdated = getContentResolver().update(localUri, localContentValues, null, null);
			    Log.e("", "" + rowsUpdated);
			    if (rowsUpdated > 0) {
			    	Toast.makeText(getApplicationContext(), "Ringtone reset successfully", Toast.LENGTH_SHORT).show();
			    }
			}
		});
	}
	
	@Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!hasSaved) {
			new File(mFileName).delete();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mCurrentCryPath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("", "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}
	
	private void confirmRecording() {
		new AlertDialog.Builder(this)
			.setTitle("Record new cry?")
			.setMessage("This will let you record a new cry for " + mContact.name + ". It will not be active until you apply it.")
			.setPositiveButton("Ok, Record", new AlertDialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					startRecording();
				}
			}).setNegativeButton("Never mind", null)
			.show();
	}

	private void startRecording() {
		ProgressDialog.show(this, "Recording...", "Press back to end recording", true, true, new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				stopRecording();
				vPlay.setEnabled(true);
				vApplyRingtone.setEnabled(true);
				mCurrentCryPath = mFileName;
			}
		});
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("", "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

}
