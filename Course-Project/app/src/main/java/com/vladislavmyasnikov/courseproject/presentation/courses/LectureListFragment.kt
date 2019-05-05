package com.vladislavmyasnikov.courseproject.presentation.courses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerLectureListFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.presentation.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.LectureListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.LectureListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.android.synthetic.main.layout_refreshing_recycler.*

class LectureListFragment : GeneralFragment() {

    @Inject
    lateinit var viewModelFactory: LectureListViewModelFactory

    @Inject
    lateinit var adapter: LectureAdapter

    private lateinit var lectureVM: LectureListViewModel
    private val disposables = CompositeDisposable()

    private val itemClickCallback = object : OnItemClickCallback {
        override fun onClick(id: Int, name: String) {
            val fragment = TaskListFragment.newInstance(id, name)
            fragmentController?.addFragmentOnTop(fragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerLectureListFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.injectLectureListFragment(this)
        lectureVM = ViewModelProviders.of(this, viewModelFactory).get(LectureListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.layout_refreshing_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(true)
        actionBarController?.setTitle(R.string.lectures_toolbar_title)
        mainPanelController?.hideMainPanel()
        swipe_refresh_layout.setOnRefreshListener { lectureVM.fetchLectures() }

        adapter.callback = itemClickCallback
        recycler_view.adapter = adapter

        disposables.add(lectureVM.loadingState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
            swipe_refresh_layout.isRefreshing = it
        })

        disposables.add(lectureVM.errors
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ForbiddenException -> App.INSTANCE.logout()
                        is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
                    }
                })

        disposables.add(lectureVM.lectures
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
            adapter.updateList(it)
        })

        if (savedInstanceState == null) {
            lectureVM.fetchLectures()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    companion object {
        fun newInstance(): LectureListFragment {
            return LectureListFragment()
        }
    }
}
