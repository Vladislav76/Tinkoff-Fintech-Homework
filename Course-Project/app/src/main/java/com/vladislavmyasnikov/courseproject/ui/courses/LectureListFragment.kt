package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Intent
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
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.di.components.DaggerFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.di.modules.FragmentModule
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModel
import javax.inject.Inject

class LectureListFragment : GeneralFragment() {

    @Inject
    lateinit var lectureListViewModel: LectureListViewModel

    @Inject
    lateinit var adapter: LectureAdapter

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

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

        val injector = DaggerFragmentInjector.builder().fragmentModule(FragmentModule(this)).contextModule(ContextModule(activity!!)).build()
        injector.injectLectureListFragment(this)

        mSwipeRefreshLayout.setOnRefreshListener { lectureListViewModel.updateLectures() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        adapter.callback = mItemClickCallback
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lectureListViewModel.lectures.observe(this, Observer {
            adapter.updateList(it)
        })

        lectureListViewModel.responseMessage.observe(this, Observer {
            if (it != null) {
                when (it) {
                    ResponseMessage.SUCCESS -> mSwipeRefreshLayout.isRefreshing = false
                    ResponseMessage.LOADING -> mSwipeRefreshLayout.isRefreshing = true
                    ResponseMessage.NO_INTERNET -> {
                        Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                    ResponseMessage.ERROR -> logout()
                }
            }
        })

        if (savedInstanceState == null) {
            mSwipeRefreshLayout.isRefreshing = true
            lectureListViewModel.updateLectures()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lectureListViewModel.resetResponseMessage()
    }

    private fun logout() {
        val intent = Intent(activity, AuthorizationActivity::class.java)
        startActivity(intent)

    }



    companion object {

        fun newInstance(): LectureListFragment {
            return LectureListFragment()
        }
    }
}
