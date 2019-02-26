package com.vladislavmyasnikov.main_android_components;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mContacts = savedInstanceState.getParcelableArrayList(EXTRA_CONTACTS_LIST);
        if (mContacts != null) {
            updateList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == START_SECOND_ACTIVITY_REQUEST_CODE) {
                mContacts = data.getParcelableArrayListExtra(SecondActivity.EXTRA_CONTACTS);
                updateList();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(EXTRA_CONTACTS_LIST, mContacts);
    }

    private void updateList() {
        if (mAdapter == null) {
            mAdapter = new ContactAdapter();
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setAdapter(mAdapter);
        }
        mAdapter.setContacts(mContacts);
    }

    public void onStartNewActivity(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivityForResult(intent, START_SECOND_ACTIVITY_REQUEST_CODE);
    }

    private ContactAdapter mAdapter;
    private ArrayList<ContactData> mContacts;

    private static final int START_SECOND_ACTIVITY_REQUEST_CODE = 1;
    private static final String EXTRA_CONTACTS_LIST = "extra_contacts_list";
}
