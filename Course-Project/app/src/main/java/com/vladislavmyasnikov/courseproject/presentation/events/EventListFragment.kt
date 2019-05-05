package com.vladislavmyasnikov.courseproject.presentation.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerEventsFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.presentation.adapters.EventAdapter
import com.vladislavmyasnikov.courseproject.presentation.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.EventListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.EventListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.android.synthetic.main.layout_refreshing_recycler.*

class EventListFragment : GeneralFragment() {

    @Inject
    lateinit var viewModelFactory: EventListViewModelFactory

    @Inject
    lateinit var adapter: EventAdapter

    private lateinit var eventListVM: EventListViewModel
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerEventsFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.inject(this)
        eventListVM = ViewModelProviders.of(this, viewModelFactory).get(EventListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.layout_refreshing_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(true)
        actionBarController?.setTitle(arguments?.getString(ARG_TITLE)!!)
        mainPanelController?.hideMainPanel()
        swipe_refresh_layout.setOnRefreshListener { eventListVM.fetchEvents() }

        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(CustomItemDecoration(5))
        adapter.viewType = EventAdapter.ViewType.DETAILED_VIEW

        disposables.add(eventListVM.loadingState.subscribe {
            swipe_refresh_layout.isRefreshing = it
        })

        disposables.add(eventListVM.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recycler_view.visibility = View.VISIBLE
                    placeholder.visibility = View.GONE
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

        if (savedInstanceState == null) {
            eventListVM.fetchEvents()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
