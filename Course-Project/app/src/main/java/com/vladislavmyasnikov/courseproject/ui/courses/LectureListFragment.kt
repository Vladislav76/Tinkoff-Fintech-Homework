package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerLectureListFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LectureListFragment : GeneralFragment() {

    @Inject
    lateinit var viewModelFactory: LectureListViewModelFactory

    @Inject
    lateinit var adapter: LectureAdapter

    private lateinit var lectureListViewModel: LectureListViewModel
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val disposables = CompositeDisposable()

    private val mItemClickCallback = object : OnItemClickCallback {
        override fun onClick(id: Int, name: String) {
            val fragment = TaskListFragment.newInstance(id, name)
            mFragmentListener?.addFragmentOnTop(fragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        mSwipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        mSwipeRefreshLayout.id = R.id.swipe_refresh_layout
        mSwipeRefreshLayout.addView(recyclerView)

        return mSwipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.lectures_toolbar_title)

        val injector = DaggerLectureListFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.injectLectureListFragment(this)
        lectureListViewModel = ViewModelProviders.of(this, viewModelFactory).get(LectureListViewModel::class.java)

        mSwipeRefreshLayout.setOnRefreshListener { lectureListViewModel.fetchLectures() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        adapter.callback = mItemClickCallback
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        disposables.add(lectureListViewModel.loadingState.subscribe {
            mSwipeRefreshLayout.isRefreshing = it
        })

        disposables.add(lectureListViewModel.lectures.subscribe {
            adapter.updateList(it)
        })

        disposables.add(lectureListViewModel.errors.subscribe {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })

        if (savedInstanceState == null) {
            lectureListViewModel.fetchLectures()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }



    companion object {

        fun newInstance(): LectureListFragment {
            return LectureListFragment()
        }
    }
}
