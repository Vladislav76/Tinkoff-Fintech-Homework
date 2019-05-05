package com.vladislavmyasnikov.courseproject.presentation.courses

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerStudentListFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.presentation.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.presentation.components.CustomItemAnimator
import com.vladislavmyasnikov.courseproject.presentation.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.StudentListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.android.synthetic.main.layout_refreshing_recycler.*

class StudentListFragment : GeneralFragment() {

    @Inject
    lateinit var studentListVMFactory: StudentListViewModelFactory

    @Inject
    lateinit var adapter: StudentAdapter

    private lateinit var studentListVM: StudentListViewModel
    private val disposables = CompositeDisposable()
    private var sortType: Int = SORTED_BY_POINTS_AND_NAME
    private var searchQuery: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerStudentListFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectStudentListFragment(this)
        studentListVM = ViewModelProviders.of(this, studentListVMFactory).get(StudentListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.layout_refreshing_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(true)
        actionBarController?.setTitle(R.string.academic_performance_toolbar_title)
        mainPanelController?.hideMainPanel()
        swipe_refresh_layout.setOnRefreshListener { studentListVM.fetchStudents() }

        recycler_view.adapter = adapter
        recycler_view.itemAnimator = CustomItemAnimator()
        recycler_view.addItemDecoration(CustomItemDecoration(10))

        if (savedInstanceState != null) {
            sortType = savedInstanceState.getInt(SORT_TYPE)
            searchQuery = savedInstanceState.getString(SEARCH_QUERY)
        }

        disposables.add(studentListVM.loadingState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    swipe_refresh_layout.isRefreshing = it
                })

        disposables.add(studentListVM.students
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateContent(it)
                })

        disposables.add(studentListVM.errors
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ForbiddenException -> App.INSTANCE.logout()
                        is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun updateContent(content: List<Student>) {
        recycler_view.visibility = View.VISIBLE
        placeholder.visibility = View.GONE
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
