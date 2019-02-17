package com.vladislavmyasnikov.main_android_components;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mReceiver = new CustomLocalBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(CustomIntentService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            }
            else {
                Toast.makeText(this, R.string.permission_read_contacts_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onGetContacts(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            startService();
        }
    }

    private void startService() {
        Intent intent = new Intent(this, CustomIntentService.class);
        startService(intent);
    }

    private void close(Intent intent) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class CustomLocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null
                    && intent.getAction().equals(CustomIntentService.BROADCAST_ACTION)
                    && intent.hasExtra(CustomIntentService.EXTRA_DATA)) {
                Log.i("LocalBroadcastReceiver", "EXTRA DATA ARE RECEIVED");
                close(intent);
            }
            else {
                Log.i("LocalBroadcastReceiver", "OTHER DATA ARE RECEIVED");
            }
        }
    }

    private CustomLocalBroadcastReceiver mReceiver;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 3;
}
