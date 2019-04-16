package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment

class RatingFragment : GeneralFragment() {

    private val mOnTitleClickListener = View.OnClickListener {
        val fragment = LectureListFragment.newInstance()
        mFragmentListener?.addFragmentOnTop(fragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.title).setOnClickListener(mOnTitleClickListener)
    }



    companion object {

        fun newInstance(): RatingFragment {
            return RatingFragment()
        }
    }
}
