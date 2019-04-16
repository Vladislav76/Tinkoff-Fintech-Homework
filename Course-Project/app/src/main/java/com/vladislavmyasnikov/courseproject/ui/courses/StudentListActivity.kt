package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener

class StudentListActivity : AppCompatActivity(), OnFragmentListener {

    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar.title = ""
        setSupportActionBar(mToolbar)

        val fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, StudentListFragment.newInstance())
                    .commit()
        }
    }

    override fun setToolbarTitle(titleId: Int) {
        mToolbar.setTitle(titleId)
    }

    override fun setToolbarTitle(title: CharSequence) {
        mToolbar.title = title
    }

    override fun addFragmentOnTop(fragment: Fragment) {}
}
