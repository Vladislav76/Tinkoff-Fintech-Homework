package com.vladislavmyasnikov.courseproject.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.models.Result
import com.vladislavmyasnikov.courseproject.data.models.User
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnBackButtonListener
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener
import com.vladislavmyasnikov.courseproject.data.network.RequestResultListener
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.RequestResultCallback
import com.vladislavmyasnikov.courseproject.ui.courses.CoursesFragment
import com.vladislavmyasnikov.courseproject.ui.events.EventsFragment
import com.vladislavmyasnikov.courseproject.ui.profile.ProfileFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentListener, RequestResultListener<Result> {

    private var mToolbar: Toolbar? = null
    private val mRequestResultCallback = RequestResultCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainPanel = findViewById<BottomNavigationView>(R.id.main_panel)
        mainPanel.setOnNavigationItemSelectedListener(this)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.title = ""
        setSupportActionBar(mToolbar)

        if (savedInstanceState == null) {
            onNavigationItemSelected(mainPanel.menu.getItem(0))

            val preferences = getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
            val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null)
            NetworkService.getInstance().fintechService.getUser(token!!).enqueue(mRequestResultCallback)
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
                    .replace(R.id.content_frame, fragment!!, tag)
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
        mToolbar?.setTitle(titleId)
    }

    override fun setToolbarTitle(title: CharSequence) {
        mToolbar?.title = title
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

    override fun onFailure(call: Call<Result>, e: Throwable) {
        Toast.makeText(this, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<Result>, response: Response<Result>) {
        val result = response.body()
        if (response.message() == "OK" && result != null && result.user != null) {

            val user = result.user
            val firstName = user.firstName
            val lastName = user.lastName
            val middleName = user.middleName
            val avatar = user.avatar

            val preferences = getSharedPreferences(USER_STORAGE_NAME, Context.MODE_PRIVATE)
            preferences.edit()
                    .putString(USER_FIRST_NAME, firstName)
                    .putString(USER_LAST_NAME, lastName)
                    .putString(USER_MIDDLE_NAME, middleName)
                    .putString(USER_AVATAR_URL, avatar)
                    .apply()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        mRequestResultCallback.setRequestResultListener(null)
    }

    companion object {

        val USER_STORAGE_NAME = "user_storage"
        val USER_FIRST_NAME = "user_first_name"
        val USER_LAST_NAME = "user_last_name"
        val USER_MIDDLE_NAME = "user_middle_name"
        val USER_AVATAR_URL = "user_avatar_url"

        private val BACK_STACK_ROOT_TAG = "root_fragment"
    }
}
