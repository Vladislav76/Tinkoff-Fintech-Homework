package com.vladislavmyasnikov.courseproject.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.courses.CoursesFragment
import com.vladislavmyasnikov.courseproject.ui.events.EventsFragment
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnBackButtonListener
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener
import com.vladislavmyasnikov.courseproject.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentListener {

    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainPanel = findViewById<BottomNavigationView>(R.id.main_panel)
        mainPanel.setOnNavigationItemSelectedListener(this)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar.title = ""
        setSupportActionBar(mToolbar)

        if (savedInstanceState == null) {
            mainPanel.selectedItemId = R.id.courses_tab
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragmentManager = supportFragmentManager
        val tag = Integer.toString(item.itemId)
        var fragment = fragmentManager.findFragmentByTag(tag)

        if (fragment == null) {
            when (item.itemId) {
                R.id.events_tab -> fragment = EventsFragment.newInstance()
                R.id.courses_tab -> fragment = CoursesFragment.newInstance()
                R.id.profile_tab -> fragment = ProfileFragment.newInstance()
                else -> return false
            }
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit()
        } else {
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0)
        }

        return true
    }

    override fun addFragmentOnTop(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun setToolbarTitle(titleId: Int) {
        mToolbar.setTitle(titleId)
    }

    override fun setToolbarTitle(title: CharSequence) {
        mToolbar.title = title
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount > 1) {
            val fragment = fragmentManager.findFragmentById(R.id.content_frame)
            if (fragment !is OnBackButtonListener || !(fragment as OnBackButtonListener).onBackPressed()) {
                fragmentManager.popBackStackImmediate()
            }
        } else {
            supportFinishAfterTransition()
        }
    }

    companion object {
        private const val BACK_STACK_ROOT_TAG = "root_fragment"
    }
}
