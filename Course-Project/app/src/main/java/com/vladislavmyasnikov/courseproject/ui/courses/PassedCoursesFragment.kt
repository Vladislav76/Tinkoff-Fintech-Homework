package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vladislavmyasnikov.courseproject.R

class PassedCoursesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_passed_courses, container, false)
    }



    companion object {

        fun newInstance(): PassedCoursesFragment {
            return PassedCoursesFragment()
        }
    }
}
