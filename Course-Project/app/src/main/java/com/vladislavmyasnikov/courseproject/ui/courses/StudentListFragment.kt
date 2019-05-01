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
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemAnimator
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class StudentListFragment : GeneralFragment() {

    @Inject
    lateinit var studentListVMFactory: StudentListViewModelFactory

    @Inject
    lateinit var adapter: StudentAdapter

    private lateinit var studentListVM: StudentListViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val disposables = CompositeDisposable()
    private var sortType: Int = SORTED_BY_POINTS_AND_NAME
    private var searchQuery: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        swipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        swipeRefreshLayout.id = R.id.swipe_refresh_layout
        swipeRefreshLayout.addView(recyclerView)

        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFragmentListener?.setToolbarTitle(R.string.academic_performance_toolbar_title)

        val injector = DaggerStudentListFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectStudentListFragment(this)
        studentListVM = ViewModelProviders.of(this, studentListVMFactory).get(StudentListViewModel::class.java)

        swipeRefreshLayout.setOnRefreshListener { studentListVM.fetchStudents() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = CustomItemAnimator()
        recyclerView.addItemDecoration(CustomItemDecoration(10))

        if (savedInstanceState != null) {
            sortType = savedInstanceState.getInt(SORT_TYPE)
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        }

        disposables.add(studentListVM.loadingState.subscribe {
            swipeRefreshLayout.isRefreshing = it
        })

        disposables.add(studentListVM.students.subscribe {
            updateContent(it)
        })

        disposables.add(studentListVM.errors.subscribe {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })

        if (savedInstanceState == null) {
            studentListVM.fetchStudents()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.students_panel, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as SearchView

        if (searchQuery != null) {
            searchItem.expandActionView()
            searchView.setQuery(searchQuery, true)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                adapter.filter.filter(s)
                searchQuery = s
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchQuery = null
                adapter.setSourceListWithoutUpdating()
                when (sortType) {
                    SORTED_BY_NAME -> adapter.sortListByStudentName()
                    SORTED_BY_POINTS_AND_NAME -> adapter.sortListByStudentPointsAndName()
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_name_action -> {
                if (sortType != SORTED_BY_NAME) {
                    adapter.sortListByStudentName()
                    sortType = SORTED_BY_NAME
                }
                return true
            }
            R.id.sort_by_points_action -> {
                if (sortType != SORTED_BY_POINTS_AND_NAME) {
                    adapter.sortListByStudentPointsAndName()
                    sortType = SORTED_BY_POINTS_AND_NAME
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(SORT_TYPE, sortType)
        savedInstanceState.putString(SEARCH_QUERY, searchQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun updateContent(content: List<Student>) {
        if (searchQuery != null) {
            adapter.setListWithoutUpdating(content)
            adapter.filter.filter(searchQuery)
        } else {
            when (sortType) {
                SORTED_BY_NAME -> adapter.setAndSortListByStudentName(content)
                SORTED_BY_POINTS_AND_NAME -> adapter.setAndSortListByStudentPointsAndName(content)
            }
        }
    }

    companion object {
        private const val SORT_TYPE = "sort_type"
        private const val SEARCH_QUERY = "search_query"
        private const val SORTED_BY_NAME = 1
        private const val SORTED_BY_POINTS_AND_NAME = 2

        fun newInstance(): StudentListFragment {
            return StudentListFragment()
        }
    }
}
