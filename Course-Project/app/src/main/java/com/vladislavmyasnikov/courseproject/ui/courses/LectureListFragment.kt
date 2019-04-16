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
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModel

class LectureListFragment : GeneralFragment() {

    private val mLectureListViewModel: LectureListViewModel by lazy {
        ViewModelProviders.of(this).get(LectureListViewModel::class.java)
    }
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

        mSwipeRefreshLayout.setOnRefreshListener { mLectureListViewModel.updateLectures() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = LectureAdapter(mItemClickCallback)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mLectureListViewModel.lectures.observe(this, Observer { lectures ->
            adapter.updateList(lectures)
        })

        mLectureListViewModel.updatingDataState.observe(this, Observer { message ->
            if (message != null) {
                if (message != "") {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }
                mSwipeRefreshLayout.isRefreshing = false
            }
        })

        mSwipeRefreshLayout.isRefreshing = true
        mLectureListViewModel.updateLectures()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLectureListViewModel.resetUpdatingDataState()
    }



    companion object {

        fun newInstance(): LectureListFragment {
            return LectureListFragment()
        }
    }
}
