package com.vladislavmyasnikov.courseproject.presentation.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerEventsFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.entities.Event
import com.vladislavmyasnikov.courseproject.presentation.adapters.EventAdapter
import com.vladislavmyasnikov.courseproject.presentation.components.CustomItemDecoration
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import kotlinx.android.synthetic.main.fragment_events_nested.*
import javax.inject.Inject

class EventsNestedFragment : GeneralFragment() {

    @Inject
    lateinit var adapter: EventAdapter

    private var isActualEventsViewingMode: Boolean = false

    private val onTitleClickCallback = View.OnClickListener {
        val fragment = EventListFragment.newInstance(arguments?.getString(ARG_TITLE)!!, isActualEventsViewingMode)
        fragmentController?.addFragmentOnTop(fragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerEventsFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.inject(this)
        isActualEventsViewingMode = arguments?.getBoolean(ARG_IS_ACTUAL_EVENTS_VIEWING_MODE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_events_nested, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.setOnClickListener(onTitleClickCallback)
        title_label.text = arguments?.getString(ARG_TITLE)!!
        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(CustomItemDecoration(5))
    }

    fun updateContent(content: List<Event>) {
        val filteredContent = content.filter { it.isActual == isActualEventsViewingMode }
        adapter.updateList(filteredContent.take(5))
        title_details_label.text = String.format("ВСЕ %d", filteredContent.size)
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_IS_ACTUAL_EVENTS_VIEWING_MODE = "is_actual_events_viewing_mode"

        fun newInstance(title: String, isActual: Boolean) =
                EventsNestedFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putBoolean(ARG_IS_ACTUAL_EVENTS_VIEWING_MODE, isActual)
                    }
                }
    }
}
