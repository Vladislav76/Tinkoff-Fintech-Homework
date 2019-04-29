package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerStudentListFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemAnimator
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModelFactory
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModelFactory
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StudentListFragment : GeneralFragment() {

    @Inject
    lateinit var studentListVMFactory: StudentListViewModelFactory

    @Inject
    lateinit var mAdapter: StudentAdapter

    private lateinit var mStudentListViewModel: StudentListViewModel
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val disposables = CompositeDisposable()
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

        val injector = DaggerStudentListFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectStudentListFragment(this)
        mStudentListViewModel = ViewModelProviders.of(this, studentListVMFactory).get(StudentListViewModel::class.java)

        mSwipeRefreshLayout.setOnRefreshListener { mStudentListViewModel.refreshStudents() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = CustomItemAnimator()
        recyclerView.addItemDecoration(CustomItemDecoration(10))

        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getInt(SORT_TYPE)
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY)
        }

        updateContent(mStudentListViewModel.students)
        mSwipeRefreshLayout.isRefreshing = mStudentListViewModel.isLoading

        disposables.add(mStudentListViewModel.studentsFetchOutcome
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    when (it) {
                        is Outcome.Progress -> mSwipeRefreshLayout.isRefreshing = it.loading
                        is Outcome.Success -> updateContent(it.data)
                        is Outcome.Failure -> Toast.makeText(activity, it.e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        )

        if (savedInstanceState == null) {
            mStudentListViewModel.fetchStudents()
        }
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
        disposables.clear()
    }

    private fun updateContent(data: List<Student>) {
        mAdapter.setStudentList(data)
        if (mSearchQuery != null) {
            mAdapter.filter.filter(mSearchQuery)
        } else {
            when (mSortType) {
                SORTED_BY_NAME -> mAdapter.sortByName(data)
                SORTED_BY_POINTS -> mAdapter.sortByPoints(data)
                UNSORTED -> mAdapter.updateList(data)
            }
        }
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
