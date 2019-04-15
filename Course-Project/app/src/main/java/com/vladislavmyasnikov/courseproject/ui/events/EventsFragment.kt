package com.vladislavmyasnikov.courseproject.ui.events

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener

class EventsFragment : Fragment() {

    private var mFragmentListener: OnFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            mFragmentListener = context
        } else {
            throw IllegalStateException("$context must implement OnFragmentListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener!!.setToolbarTitle(R.string.events_toolbar_title)
    }

    override fun onDetach() {
        super.onDetach()
        mFragmentListener = null
    }

    companion object {

        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }
}
