package com.vladislavmyasnikov.courseproject.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.core.NetworkService;
import com.vladislavmyasnikov.courseproject.models.Login;
import com.vladislavmyasnikov.courseproject.models.User;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity implements Callback<User> {

    private EditText mEmailInputField;
    private EditText mPasswordInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mEmailInputField = findViewById(R.id.input_email_field);
        mPasswordInputField = findViewById(R.id.input_password_field);
    }

    public void onLoginClicked(View view) {
        String email = mEmailInputField.getText().toString();
        String password = mPasswordInputField.getText().toString();

        NetworkService.getInstance().getFintechService().getAccess(new Login(email, password)).enqueue(this);
    }

    @Override
    public void onFailure(Call<User> call, Throwable e) {
        Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        User user = response.body();
        if (user != null) {
            //TODO: implement authorization and data saving
        }
        else {
            Toast.makeText(this, R.string.incorrect_authorization_data_message, Toast.LENGTH_SHORT).show();
        }
    }
}
