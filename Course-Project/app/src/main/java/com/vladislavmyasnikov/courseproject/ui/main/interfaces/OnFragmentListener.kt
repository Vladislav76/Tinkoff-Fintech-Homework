package com.vladislavmyasnikov.courseproject.ui.main.interfaces

import androidx.fragment.app.Fragment

interface OnFragmentListener {

    fun addFragmentOnTop(fragment: Fragment)
    fun setToolbarTitle(titleId: Int)
    fun setToolbarTitle(title: CharSequence)
}
