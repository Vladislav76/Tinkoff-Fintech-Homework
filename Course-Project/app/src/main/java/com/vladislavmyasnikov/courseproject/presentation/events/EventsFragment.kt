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
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.EventListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.EventListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_events.*
import javax.inject.Inject

class EventsFragment : GeneralFragment() {

    @Inject
    lateinit var eventListVMFactory: EventListViewModelFactory

    private lateinit var eventListVM: EventListViewModel
    private lateinit var actualEventsFragment: EventsNestedFragment
    private lateinit var pastEventsFragment: EventsNestedFragment
    private var disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerEventsFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.inject(this)
        eventListVM = ViewModelProviders.of(this, eventListVMFactory).get(EventListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        addChildFragment(CONTENT_FRAME_1_TAG)
        addChildFragment(CONTENT_FRAME_2_TAG)
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(false)
        actionBarController?.setTitle(R.string.events_toolbar_title)
        mainPanelController?.showMainPanel()
        swipe_refresh_layout.setOnRefreshListener { eventListVM.fetchEvents() }

        disposables.add(eventListVM.loadingState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    swipe_refresh_layout.isRefreshing = it
                })

        disposables.add(eventListVM.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    actualEventsFragment.updateContent(it)
                    pastEventsFragment.updateContent(it)
                })

        disposables.add(eventListVM.errors
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
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

    private fun addChildFragment(tag: String) {
        val fragmentManager = childFragmentManager
        var fragment = fragmentManager.findFragmentByTag(tag)
        val containerId: Int

        if (fragment == null) {
            when (tag) {
                CONTENT_FRAME_1_TAG -> {
                    fragment = EventsNestedFragment.newInstance(resources.getString(R.string.actual_events_label), isActual = true)
                    actualEventsFragment = fragment
                    containerId = R.id.content_frame_1
                }
                CONTENT_FRAME_2_TAG -> {
                    fragment = EventsNestedFragment.newInstance(resources.getString(R.string.past_events_label), isActual = false)
                    pastEventsFragment = fragment
                    containerId = R.id.content_frame_2
                }
                else -> return
            }
            fragmentManager.beginTransaction().replace(containerId, fragment).commit()
        }
    }

    companion object {
        private const val CONTENT_FRAME_1_TAG = "content_frame_1"
        private const val CONTENT_FRAME_2_TAG = "content_frame_2"

        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }
}
