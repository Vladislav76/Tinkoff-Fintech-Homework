package com.vladislavmyasnikov.main_android_components;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == START_SECOND_ACTIVITY_REQUEST_CODE) {
                ArrayList<ContactData> contacts = data.getParcelableArrayListExtra(CustomIntentService.EXTRA_DATA);
                updateList(contacts);
            }
        }
    }

    private void updateList(ArrayList<ContactData> contacts) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ContactAdapter(contacts));
    }

    public void onStartNewActivity(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivityForResult(intent, START_SECOND_ACTIVITY_REQUEST_CODE);
    }

    private int START_SECOND_ACTIVITY_REQUEST_CODE = 7;
}
