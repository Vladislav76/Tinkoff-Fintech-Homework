package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.utilities.DataUpdater
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnRefreshLayoutListener
import com.vladislavmyasnikov.courseproject.ui.components.UserView

class AcademicPerformanceFragment : Fragment() {

    private var mRefreshLayoutListener: OnRefreshLayoutListener? = null
    private var mUser1View: UserView? = null
    private var mUser2View: UserView? = null
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val data = msg.data
            val points = data.getIntArray(DataUpdater.getUPDATED_POINTS_DATA())
            if (points != null && mRefreshLayoutListener != null) {
                mUser1View!!.setBadgeCount(points[0])
                mUser2View!!.setBadgeCount(points[1])
                mRefreshLayoutListener!!.stopRefreshing()
            }
        }
    }

    private val mOnTitleListener = View.OnClickListener {
        val intent = Intent(context, StudentListActivity::class.java)
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnRefreshLayoutListener) {
            mRefreshLayoutListener = parentFragment as OnRefreshLayoutListener?
        } else {
            throw IllegalStateException(parentFragment!!.toString() + " must implement OnRefreshLayoutListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mUser1View = view.findViewById(R.id.user_1)
        mUser2View = view.findViewById(R.id.user_2)
        view.findViewById<View>(R.id.title).setOnClickListener(mOnTitleListener)
    }

    override fun onDetach() {
        super.onDetach()
        mRefreshLayoutListener = null
    }

    fun updateBadges() {
        DataUpdater.newInstance(mHandler, CURRENT_HARDCODED_NUMBER_OF_USER_ICONS).start()
    }

    companion object {

        private val CURRENT_HARDCODED_NUMBER_OF_USER_ICONS = 2

        fun newInstance(): AcademicPerformanceFragment {
            return AcademicPerformanceFragment()
        }
    }
}
