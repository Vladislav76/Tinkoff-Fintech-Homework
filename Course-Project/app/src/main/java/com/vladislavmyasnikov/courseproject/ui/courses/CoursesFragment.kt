package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerCoursesFragmentInjector
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.CourseViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.CourseViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CoursesFragment : GeneralFragment() {

    @Inject
    lateinit var courseVMFactory: CourseViewModelFactory

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var courseVM: CourseViewModel
    private lateinit var studentFragment: TopStudentsFragment
    private lateinit var courseFragment: RatingFragment
    private var disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerCoursesFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.inject(this)
        courseVM = ViewModelProviders.of(this, courseVMFactory).get(CourseViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFragmentListener?.setToolbarTitle(R.string.courses_toolbar_title)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { courseVM.fetchCourse() }

        addChildFragment(CONTENT_FRAME_1_TAG)
        addChildFragment(CONTENT_FRAME_2_TAG)
        addChildFragment(CONTENT_FRAME_3_TAG)

        disposables.add(courseVM.loadingState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    swipeRefreshLayout.isRefreshing = it
                }
        )

        disposables.add(courseVM.students
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    studentFragment.updateContent(it)
                }
        )

        disposables.add(courseVM.course
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    courseFragment.updateContent(it)
                }
        )

        disposables.add(courseVM.errors.subscribe {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })

        if (savedInstanceState == null) {
            courseVM.fetchCourse()
        }
    }

    private fun addChildFragment(tag: String) {
        val fragmentManager = childFragmentManager
        var fragment = fragmentManager.findFragmentByTag(tag)
        val containerId: Int

        if (fragment == null) {
            when (tag) {
                CONTENT_FRAME_1_TAG -> {
                    fragment = TopStudentsFragment.newInstance()
                    studentFragment = fragment
                    containerId = R.id.content_frame_1
                }
                CONTENT_FRAME_2_TAG -> {
                    fragment = RatingFragment.newInstance()
                    courseFragment = fragment
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
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
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
