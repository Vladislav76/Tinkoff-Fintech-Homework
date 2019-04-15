package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnRefreshLayoutListener
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class CoursesFragment : GeneralFragment(), OnRefreshLayoutListener {

    private var mAcademicPerformanceFragment: AcademicPerformanceFragment? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener!!.setToolbarTitle(R.string.courses_toolbar_title)

        addChildFragment(CONTENT_FRAME_1_TAG)
        addChildFragment(CONTENT_FRAME_2_TAG)
        addChildFragment(CONTENT_FRAME_3_TAG)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout!!.setOnRefreshListener { mAcademicPerformanceFragment!!.updateBadges() }
    }

    override fun stopRefreshing() {
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    private fun addChildFragment(tag: String) {
        val fragmentManager = childFragmentManager
        var fragment = fragmentManager.findFragmentByTag(CONTENT_FRAME_1_TAG)
        val containerId: Int

        if (fragment == null) {
            when (tag) {
                CONTENT_FRAME_1_TAG -> {
                    mAcademicPerformanceFragment = AcademicPerformanceFragment.newInstance()
                    fragment = mAcademicPerformanceFragment
                    containerId = R.id.content_frame_1
                }
                CONTENT_FRAME_2_TAG -> {
                    fragment = RatingFragment.newInstance()
                    containerId = R.id.content_frame_2
                }
                CONTENT_FRAME_3_TAG -> {
                    fragment = PassedCoursesFragment.newInstance()
                    containerId = R.id.content_frame_3
                }
                else -> return
            }
            fragmentManager.beginTransaction().replace(containerId, fragment!!).commit()
        }
    }

    companion object {

        private val CONTENT_FRAME_1_TAG = "content_frame_1"
        private val CONTENT_FRAME_2_TAG = "content_frame_2"
        private val CONTENT_FRAME_3_TAG = "content_frame_3"

        fun newInstance(): CoursesFragment {
            return CoursesFragment()
        }
    }
}
