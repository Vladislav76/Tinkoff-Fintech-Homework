package com.vladislavmyasnikov.main_android_components;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CustomIntentService extends IntentService {

    public CustomIntentService() {
        super("com.vladislavmyasnikov.main_android_components.CustomIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent newIntent = new Intent(BROADCAST_ACTION).putExtra(EXTRA_DATA, getContacts());
            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
        }
    }

    private ArrayList<ContactData> getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<ContactData> contacts = new ArrayList<>();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    ArrayList<String> phoneNumbers = new ArrayList<>();

                    if (Integer.parseInt(hasPhone) > 0) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ contactId, null, null);

                        if (phones != null) {
                            try {
                                while (phones.moveToNext()) {
                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneNumbers.add(phoneNumber);
                                }
                            }
                            finally {
                                phones.close();
                            }
                        }
                    }
                    contacts.add(new ContactData(displayName, phoneNumbers));
                }
            }
            finally {
                cursor.close();
            }
        }
        return contacts;
    }

    public static final String BROADCAST_ACTION = "broadcast_action";
    public static final String EXTRA_DATA = "extra_data";
}
