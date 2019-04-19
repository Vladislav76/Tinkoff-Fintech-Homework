package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemAnimator
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel

class StudentListFragment : GeneralFragment() {

    private val mStudentListViewModel: StudentListViewModel by lazy {
        ViewModelProviders.of(this).get(StudentListViewModel::class.java)
    }

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAdapter: StudentAdapter
    private var mSortType: Int = UNSORTED
    private var mSearchQuery: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        mSwipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        mSwipeRefreshLayout.id = R.id.swipe_refresh_layout
        mSwipeRefreshLayout.addView(recyclerView)

        return mSwipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.academic_performance_toolbar_title)

        mSwipeRefreshLayout.setOnRefreshListener { mStudentListViewModel.updateStudents() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        mAdapter = StudentAdapter(activity!!, StudentAdapter.ViewType.LINEAR_VIEW)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = CustomItemAnimator()
        recyclerView.addItemDecoration(CustomItemDecoration(10))

        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getInt(SORT_TYPE)
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY)
        }

        mStudentListViewModel.students.observe(this, Observer { students ->
            mAdapter.setStudentList(students)
            if (mSearchQuery != null) {
                mAdapter.filter.filter(mSearchQuery)
            } else {
                when (mSortType) {
                    SORTED_BY_NAME -> mAdapter.sortByName(students)
                    SORTED_BY_POINTS -> mAdapter.sortByPoints(students)
                    UNSORTED -> mAdapter.updateList(students)
                }
            }
        })

        mStudentListViewModel.messageState.observe(this, Observer { message ->
            if (message != null) {
                if (message != "") {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }
                mSwipeRefreshLayout.isRefreshing = false
            }
        })

        mSwipeRefreshLayout.isRefreshing = true
        mStudentListViewModel.updateStudents()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.students_panel, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as SearchView

        if (mSearchQuery != null) {
            searchItem.expandActionView()
            searchView.setQuery(mSearchQuery, true)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                mAdapter.filter.filter(s)
                mSearchQuery = s
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mSearchQuery = null
                when (mSortType) {
                    SORTED_BY_NAME -> mAdapter.sortByName()
                    SORTED_BY_POINTS -> mAdapter.sortByPoints()
                    UNSORTED -> mAdapter.sortByDefault()
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_name_action -> {
                if (mSortType != SORTED_BY_NAME) {
                    mAdapter.sortByName()
                    mSortType = SORTED_BY_NAME
                }
                return true
            }
            R.id.sort_by_points_action -> {
                if (mSortType != SORTED_BY_POINTS) {
                    mAdapter.sortByPoints()
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
        savedInstanceState.putString(SEARCH_QUERY, mSearchQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
        mStudentListViewModel.resetMessageState()
    }



    companion object {

        private const val SORT_TYPE = "sort_type"
        private const val SEARCH_QUERY = "search_query"
        private const val SORTED_BY_NAME = 1
        private const val SORTED_BY_POINTS = 2
        private const val UNSORTED = 3

        fun newInstance(): StudentListFragment {
            return StudentListFragment()
        }
    }
}
