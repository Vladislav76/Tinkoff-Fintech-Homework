package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.models.User
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemAnimator
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.data.models.Result

import java.util.ArrayList
import java.util.Random
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.RequestResultCallback
import com.vladislavmyasnikov.courseproject.data.network.RequestResultListener
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel
import retrofit2.Call
import retrofit2.Response

class StudentListFragment : GeneralFragment(), RequestResultListener<List<Result>> {

    private var mStudentListViewModel: StudentListViewModel? = null
    private val mRequestResultCallback = RequestResultCallback(this)
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAdapter: StudentAdapter? = null
    private var mSortType: Int = UNSORTED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        val layout = SwipeRefreshLayout(inflater.context)
        layout.id = R.id.swipe_refresh_layout
        layout.addView(recyclerView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.academic_performance_toolbar_title)

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout?.setOnRefreshListener { updateData() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        mAdapter = StudentAdapter(activity!!)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = CustomItemAnimator()
        recyclerView.addItemDecoration(CustomItemDecoration(10))

        mStudentListViewModel = ViewModelProviders.of(this).get(StudentListViewModel::class.java)
        mStudentListViewModel!!.students.observe(this, Observer { students ->
            if (students != null) {
                when (mSortType) {
                    SORTED_BY_NAME -> mAdapter?.sortByName(students)
                    SORTED_BY_POINTS -> mAdapter?.sortByPoints(students)
                    UNSORTED -> mAdapter?.setStudentList(students)
                }
            }
        })

        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getInt(SORT_TYPE)
        }
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.students_panel, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isEmpty()) {
                    mAdapter?.sortByDefault()
                    when (mSortType) {
                        SORTED_BY_NAME -> mAdapter?.sortByName()
                        SORTED_BY_POINTS -> mAdapter?.sortByPoints()
                    }
                } else {
                    mAdapter?.filter?.filter(s)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_name_action -> {
                if (mSortType != SORTED_BY_NAME) {
                    mAdapter?.sortByName()
                    mSortType = SORTED_BY_NAME
                }
                return true
            }
            R.id.sort_by_points_action -> {
                if (mSortType != SORTED_BY_POINTS) {
                    mAdapter?.sortByPoints()
                    mSortType = SORTED_BY_POINTS
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(SORT_TYPE, mSortType)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRequestResultCallback.setRequestResultListener(null)
    }

    override fun onFailure(call: Call<List<Result>>, e: Throwable) {
        Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
        mSwipeRefreshLayout?.isRefreshing = false
    }

    override fun onResponse(call: Call<List<Result>>, response: Response<List<Result>>) {
        if (response.message() == "OK") {
            mStudentListViewModel!!.updateStudents(response.body()?.get(1)?.students!!)
        }
        mSwipeRefreshLayout?.isRefreshing = false
    }

    private fun updateData() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mStudentListViewModel!!.recentRequestTime > 10_000) {
            refreshData()
        } else {
            mSwipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun refreshData() {
        mSwipeRefreshLayout?.isRefreshing = true
        println("Start fetching data from server...")
        val preferences = activity!!.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null)
        NetworkService.getInstance().fintechService.getStudents(token!!).enqueue(mRequestResultCallback)
        mStudentListViewModel!!.recentRequestTime = System.currentTimeMillis()
        println("token: $token")
    }

    companion object {

        private const val SORT_TYPE = "sort_type"
        private const val SORTED_BY_NAME = 1
        private const val SORTED_BY_POINTS = 2
        private const val UNSORTED = 3

        fun newInstance(): StudentListFragment {
            return StudentListFragment()
        }
    }
}
