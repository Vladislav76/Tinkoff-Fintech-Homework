package com.vladislavmyasnikov.courseproject.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.vladislavmyasnikov.courseproject.models.User;

import java.util.ArrayList;
import java.util.Random;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ContactReadingIntentService extends IntentService {

    public static final String READ_CONTACTS_ACTION = "read_contacts_action";
    public static final String CONTACTS_DATA = "contacts_data";

    public ContactReadingIntentService() {
        super("ContactReadingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (READ_CONTACTS_ACTION.equals(action)) {
                handleContactsReadingAction();
            }
        }
    }

    private ArrayList<User> getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<User> contacts = new ArrayList<>();
        Random random = new Random();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String[] strings = displayName != null ? displayName.split(" ", 2) : new String[0];
                    String name = strings.length > 0 ? strings[0] : "";
                    String surname = strings.length > 1 ? strings[1] : "";
                    contacts.add(new User(name, surname, random.nextInt(500)));
                }
            }
            finally {
                cursor.close();
            }
        }
        return contacts;
    }

    private void handleContactsReadingAction() {
        Intent intent = new Intent(READ_CONTACTS_ACTION).putExtra(CONTACTS_DATA, getContacts());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void startContactsReadingAction(Context context) {
        Intent intent = new Intent(context, ContactReadingIntentService.class);
        intent.setAction(READ_CONTACTS_ACTION);
        context.startService(intent);
    }
}
