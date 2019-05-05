package com.vladislavmyasnikov.courseproject.presentation.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.presentation.courses.CoursesFragment
import com.vladislavmyasnikov.courseproject.presentation.events.EventsFragment
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.ActionBarController
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.FragmentController
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.MainPanelController
import com.vladislavmyasnikov.courseproject.presentation.profile.ProfileFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, FragmentController, ActionBarController, MainPanelController {

    private lateinit var toolbar: Toolbar
    private lateinit var mainPanel: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPanel = findViewById(R.id.main_panel)
        mainPanel.setOnNavigationItemSelectedListener(this)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            mainPanel.selectedItemId = R.id.courses_tab
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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

    override fun setDisplayHomeAsUpEnabled(value: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    override fun setTitle(titleId: Int) {
        toolbar.setTitle(titleId)
    }

    override fun setTitle(title: CharSequence) {
        toolbar.title = title
    }

    override fun showMainPanel() {
        mainPanel.visibility = View.VISIBLE
    }

    override fun hideMainPanel() {
        mainPanel.visibility = View.GONE
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) fragmentManager.popBackStackImmediate() else supportFinishAfterTransition()
    }

    companion object {
        private const val BACK_STACK_ROOT_TAG = "root_fragment"
    }
}
