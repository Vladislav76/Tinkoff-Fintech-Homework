package com.vladislavmyasnikov.courseproject.presentation.main

import android.content.Context

import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.FragmentController
import androidx.fragment.app.Fragment
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.ActionBarController
import com.vladislavmyasnikov.courseproject.presentation.main.interfaces.MainPanelController

open class GeneralFragment : Fragment() {

    protected var fragmentController: FragmentController? = null
    protected var actionBarController: ActionBarController? = null
    protected var mainPanelController: MainPanelController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentController) fragmentController = context
        if (context is ActionBarController) actionBarController = context
        if (context is MainPanelController) mainPanelController = context
        if (fragmentController == null || actionBarController == null || mainPanelController == null) {
            throw IllegalStateException("$context does't implement FragmentController, ActionBarController or MainPanelController interfaces")
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentController = null
    }
}
