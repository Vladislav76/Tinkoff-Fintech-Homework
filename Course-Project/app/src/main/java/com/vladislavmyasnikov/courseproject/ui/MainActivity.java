package com.vladislavmyasnikov.courseproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnBackButtonListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnToolbarChangedListener;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnToolbarChangedListener, OnFragmentListener {

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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, EventsFragment.newInstance(), "0")
                    .addToBackStack("0")
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = Integer.toString(item.getOrder());
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragment == null) {
            switch (item.getItemId()) {
                case R.id.events_action:
                    fragment = EventsFragment.newInstance();
                    break;
                case R.id.courses_action:
                    fragment = CoursesFragment.newInstance();
                    break;
                case R.id.profile_action:
                    fragment = ProfileFragment.newInstance();
                    break;
                default:
                    return false;
            }
        }
        fragmentTransaction.replace(R.id.content_frame, fragment, tag)
                .addToBackStack(tag)
                .commit();
        return true;
    }

    @Override
    public void addFragmentOnTop(@NonNull Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (!(fragment instanceof OnBackButtonListener) ||
                    !((OnBackButtonListener) fragment).onBackPressed()) {
                fragmentManager.popBackStackImmediate();

                fragment = fragmentManager.findFragmentById(R.id.content_frame);
                System.out.println("TopFragmentTag = " + fragment.getTag());
                int itemId = getItemId(fragment.getTag());
                if (itemId >= 0) {
                    mMainPanel.getMenu().findItem(itemId).setChecked(true);
                }
            }
        }
        else {
            supportFinishAfterTransition();
        }
    }

    @Override
    public void setToolbarTitle(int titleId) {
        mToolbar.setTitle(titleId);
    }

    private int getItemId(String tag) {
        switch (tag) {
            case "0":
                return R.id.events_action;
            case "1":
                return R.id.courses_action;
            case "2":
                return R.id.profile_action;
            default:
                return -1;
        }
    }

    private Toolbar mToolbar;
    private BottomNavigationView mMainPanel;
}
