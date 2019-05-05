package com.vladislavmyasnikov.courseproject.presentation.courses

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
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.CourseViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.CourseViewModelFactory
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
        addChildFragment(CONTENT_FRAME_1_TAG)
        addChildFragment(CONTENT_FRAME_2_TAG)
        addChildFragment(CONTENT_FRAME_3_TAG)
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(false)
        actionBarController?.setTitle(R.string.courses_toolbar_title)
        mainPanelController?.showMainPanel()

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { courseVM.fetchCourse() }

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
            when (it) {
                is ForbiddenException -> App.INSTANCE.logout()
                is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
            }
        })

        if (savedInstanceState == null) {
            courseVM.fetchCourse()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
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

    companion object {
        private const val CONTENT_FRAME_1_TAG = "content_frame_1"
        private const val CONTENT_FRAME_2_TAG = "content_frame_2"
        private const val CONTENT_FRAME_3_TAG = "content_frame_3"

        fun newInstance(): CoursesFragment {
            return CoursesFragment()
        }
    }
}
