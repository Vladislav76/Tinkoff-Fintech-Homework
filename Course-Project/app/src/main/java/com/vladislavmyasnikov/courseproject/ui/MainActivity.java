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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainPanel = findViewById(R.id.main_panel);
        mMainPanel.setOnNavigationItemSelectedListener(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            addFragmentOnTop(EventsFragment.newInstance(), Integer.toString(R.id.events_action));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = Integer.toString(item.getItemId());
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

                int itemId = Integer.parseInt(fragment.getTag());
                if (itemId >= 0) {
                    mMainPanel.getMenu().findItem(itemId).setChecked(true);
                }
            }
        }
        else {
            supportFinishAfterTransition();
        }
    }

    private Toolbar mToolbar;
    private BottomNavigationView mMainPanel;
}
