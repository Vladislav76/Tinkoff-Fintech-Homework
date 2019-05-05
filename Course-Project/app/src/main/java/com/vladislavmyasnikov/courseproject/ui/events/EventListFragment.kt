package com.vladislavmyasnikov.courseproject.ui.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerEventsFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.ui.adapters.EventAdapter
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.EventListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.EventListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class EventListFragment : GeneralFragment() {

    @Inject
    lateinit var viewModelFactory: EventListViewModelFactory

    @Inject
    lateinit var adapter: EventAdapter

    private lateinit var eventListVM: EventListViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerEventsFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.inject(this)
        eventListVM = ViewModelProviders.of(this, viewModelFactory).get(EventListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CustomItemDecoration(5))
        adapter.viewType = EventAdapter.ViewType.DETAILED_VIEW

        swipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        swipeRefreshLayout.id = R.id.swipe_refresh_layout
        swipeRefreshLayout.addView(recyclerView)

        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentListener?.setToolbarTitle(arguments?.getString(ARG_TITLE)!!)
        swipeRefreshLayout.setOnRefreshListener { eventListVM.fetchEvents() }

        if (savedInstanceState == null) {
            eventListVM.fetchEvents()
        }
    }

    override fun onStart() {
        super.onStart()
        disposables.add(eventListVM.loadingState.subscribe {
            swipeRefreshLayout.isRefreshing = it
        })

        disposables.add(eventListVM.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.updateList(it.filter {event ->
                        event.isActual == arguments?.getBoolean(ARG_IS_ACTUAL_EVENTS_VIEWING_MODE)
                    })
                })

        disposables.add(eventListVM.errors.subscribe {
            when (it) {
                is ForbiddenException -> App.INSTANCE.logout()
                is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_IS_ACTUAL_EVENTS_VIEWING_MODE = "is_actual_events_viewing_mode"

        fun newInstance(title: String, isActual: Boolean): EventListFragment =
                EventListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putBoolean(ARG_IS_ACTUAL_EVENTS_VIEWING_MODE, isActual)
                    }
                }
    }
}
