package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.core.NetworkService;
import com.vladislavmyasnikov.courseproject.models.Result;
import com.vladislavmyasnikov.courseproject.models.User;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnBackButtonListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentListener, Callback<Result> {

    public static final String USER_STORAGE_NAME = "user_storage";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_MIDDLE_NAME = "user_middle_name";
    public static final String USER_AVATAR_URL = "user_avatar_url";

    private Toolbar mToolbar;
    private BottomNavigationView mMainPanel;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainPanel = findViewById(R.id.main_panel);
        mMainPanel.setOnNavigationItemSelectedListener(this);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            onNavigationItemSelected(mMainPanel.getMenu().getItem(0));

            SharedPreferences preferences = getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE);
            String token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null);
            NetworkService.getInstance().getFintechService().getUser(token).enqueue(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = Integer.toString(item.getItemId());
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            switch (item.getItemId()) {
                case R.id.events_tab:
                    fragment = EventsFragment.newInstance();
                    break;
                case R.id.courses_tab:
                    fragment = CoursesFragment.newInstance();
                    break;
                case R.id.profile_tab:
                    fragment = ProfileFragment.newInstance();
                    break;
                default:
                    return false;
            }
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        } else {
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0);
        }

        return true;
    }

    @Override
    public void addFragmentOnTop(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void setToolbarTitle(int titleId) {
        mToolbar.setTitle(titleId);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(fragment instanceof OnBackButtonListener) ||
                    !((OnBackButtonListener) fragment).onBackPressed()) {
                fragmentManager.popBackStackImmediate();
            }
        } else {
            setResult(RESULT_OK);
            supportFinishAfterTransition();
        }
    }

    @Override
    public void onFailure(Call<Result> call, Throwable e) {
        Toast.makeText(this, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Call<Result> call, Response<Result> response) {
        Result result = response.body();
        if (response.message().equals("OK") && result != null && result.getUser() != null) {

            User user = result.getUser();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String middleName = user.getMiddleName();
            String avatar = user.getAvatar();

            SharedPreferences preferences = getSharedPreferences(USER_STORAGE_NAME, Context.MODE_PRIVATE);
            preferences.edit()
                    .putString(USER_FIRST_NAME, firstName)
                    .putString(USER_LAST_NAME, lastName)
                    .putString(USER_MIDDLE_NAME, middleName)
                    .putString(USER_AVATAR_URL, avatar)
                    .apply();
        }
    }
}
