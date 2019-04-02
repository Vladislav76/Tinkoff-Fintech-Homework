package com.vladislavmyasnikov.courseproject.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.services.NetworkService;
import com.vladislavmyasnikov.courseproject.data.models.Login;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity implements Callback<Void> {

    public static final String COOKIES_STORAGE_NAME = "cookies_storage";
    public static final String AUTHORIZATION_TOKEN = "authorization_token";

    private static final int START_WORK_CODE = 1;
    private TextInputEditText mEmailInputField;
    private TextInputEditText mPasswordInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mEmailInputField = findViewById(R.id.input_email_field);
        mPasswordInputField = findViewById(R.id.input_password_field);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_WORK_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable e) {
        Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.message().equals("OK")) {
            Headers headers = response.headers();

            String[] cookieData = headers.get("Set-Cookie").split("; ");
            String token = cookieData[0];

            SharedPreferences preferences = getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE);
            preferences.edit()
                    .putString(AUTHORIZATION_TOKEN, token)
                    .apply();
            startWork();
        } else {
            Toast.makeText(this, R.string.incorrect_authorization_data_message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginClicked(View view) {
        String email = mEmailInputField.getText().toString();
        String password = mPasswordInputField.getText().toString();

        NetworkService.getInstance().getFintechService().getAccess(new Login(email, password)).enqueue(this);
    }

    private void startWork() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, START_WORK_CODE);
    }
}
