package com.vladislavmyasnikov.courseproject.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment

class EventsFragment : GeneralFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.events_toolbar_title)
    }



    companion object {

        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }
}
