package com.vladislavmyasnikov.courseproject.ui.courses;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.data.models.User;
import com.vladislavmyasnikov.courseproject.services.ContactReadingIntentService;
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UsersListActivity extends AppCompatActivity implements OnFragmentListener {

    private UsersListBroadcastReceiver mReceiver;
    private UsersListFragment mFragment;
    private Toolbar mToolbar;

    private static final int READ_CONTACTS_PERMISSION_REQUEST = 1;
    private static final String USERS_LIST_FRAGMENT_TAG = "users_list_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragment = UsersListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mFragment, USERS_LIST_FRAGMENT_TAG)
                    .commit();
        } else {
            mFragment = (UsersListFragment) fragmentManager.findFragmentByTag(USERS_LIST_FRAGMENT_TAG);
        }

        mReceiver = new UsersListBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ContactReadingIntentService.READ_CONTACTS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        getUsersList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContactReadingIntentService.startContactsReadingAction(this);
            } else {
                Toast.makeText(this, R.string.permission_read_contacts_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void setToolbarTitle(int titleId) {
        mToolbar.setTitle(titleId);
    }

    @Override
    public void setToolbarTitle(CharSequence title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void addFragmentOnTop(Fragment fragment) { }

    private void getUsersList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST);
        } else {
            ContactReadingIntentService.startContactsReadingAction(this);
        }
    }

    private class UsersListBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ContactReadingIntentService.READ_CONTACTS_ACTION.equals(intent.getAction())
                    && intent.hasExtra(ContactReadingIntentService.CONTACTS_DATA)) {
                ArrayList<User> users = intent.getParcelableArrayListExtra(ContactReadingIntentService.CONTACTS_DATA);
                mFragment.updateList(users);
            }
        }
    }
}
