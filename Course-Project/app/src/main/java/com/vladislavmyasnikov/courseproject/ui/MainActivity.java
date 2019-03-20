package com.vladislavmyasnikov.courseproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnBackButtonListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentListener {

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
        }
        else {
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
        }
        else {
            supportFinishAfterTransition();
        }
    }
}
