package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStartListener
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStopListener

class CoursesFragment : GeneralFragment(), UpdateStopListener {

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val mUpdateStartListeners: MutableList<UpdateStartListener> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.courses_toolbar_title)

        addChildFragment(CONTENT_FRAME_1_TAG)
        addChildFragment(CONTENT_FRAME_2_TAG)
        addChildFragment(CONTENT_FRAME_3_TAG)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener { mUpdateStartListeners.forEach { it.startUpdate() } }
    }

    override fun stopUpdate(message: String) {
        mSwipeRefreshLayout.isRefreshing = false  //TODO: when all listeners stop updating
        if (message != "") {
            Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addChildFragment(tag: String) {
        val fragmentManager = childFragmentManager
        var fragment = fragmentManager.findFragmentByTag(tag)
        val containerId: Int

        if (fragment == null) {
            when (tag) {
                CONTENT_FRAME_1_TAG -> {
                    fragment = AcademicPerformanceFragment.newInstance()
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
            fragmentManager.beginTransaction().replace(containerId, fragment).commit()
        }
        if (fragment is UpdateStartListener) {
            mUpdateStartListeners.add(fragment)
        }
    }



    companion object {

        private const val CONTENT_FRAME_1_TAG = "content_frame_1"
        private const val CONTENT_FRAME_2_TAG = "content_frame_2"
        private const val CONTENT_FRAME_3_TAG = "content_frame_3"

        fun newInstance(): CoursesFragment {
            return CoursesFragment()
        }
    }
}
