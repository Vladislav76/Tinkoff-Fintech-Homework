package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.models.Result
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.data.network.RequestResultListener
import com.vladislavmyasnikov.courseproject.data.network.RequestResultCallback
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Response

class LectureListFragment : GeneralFragment(), RequestResultListener<Result> {

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mLectureListViewModel: LectureListViewModel? = null
    private val mRequestResultCallback = RequestResultCallback(this)
    private val mItemClickCallback = object : OnItemClickCallback {
        override fun onClick(id: Int, name: String) {
            val fragment = TaskListFragment.newInstance(id, name)
            mFragmentListener!!.addFragmentOnTop(fragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        val layout = SwipeRefreshLayout(inflater.context)
        layout.id = R.id.swipe_refresh_layout
        layout.addView(recyclerView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener!!.setToolbarTitle(R.string.lectures_toolbar_title)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = LectureAdapter(mItemClickCallback)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout!!.setOnRefreshListener { refreshData() }

        mLectureListViewModel = ViewModelProviders.of(this).get(LectureListViewModel::class.java)
        mLectureListViewModel!!.lectures.observe(this, Observer { lectures ->
            if (lectures != null && lectures.size > 0) {
                adapter.updateList(lectures)
            } else {
                mSwipeRefreshLayout!!.isRefreshing = true
                refreshData()
            }
        })
    }

    override fun onFailure(call: Call<Result>, e: Throwable) {
        Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    override fun onResponse(call: Call<Result>, response: Response<Result>) {
        val result = response.body()
        if (response.message() == "OK" && result != null && result.lectures != null) {
            mLectureListViewModel!!.updateLectures(result.lectures)
        }
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mRequestResultCallback.setRequestResultListener(null)
    }

    private fun refreshData() {
        val preferences = activity!!.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null)
        NetworkService.getInstance().fintechService.getLectures(token!!).enqueue(mRequestResultCallback)
    }

    companion object {

        fun newInstance(): LectureListFragment {
            return LectureListFragment()
        }
    }
}
