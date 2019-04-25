package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.di.modules.FragmentModule
import com.vladislavmyasnikov.courseproject.ui.adapters.TaskAdapter
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.TaskListViewModel
import javax.inject.Inject

class TaskListFragment : GeneralFragment() {

    @Inject
    lateinit var taskListViewModel: TaskListViewModel

    @Inject
    lateinit var adapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(arguments!!.getString(TITLE_ARG)!!)

        val injector = DaggerFragmentInjector.builder().fragmentModule(FragmentModule(this)).contextModule(ContextModule(activity!!)).build()
        injector.injectTaskListFragment(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        taskListViewModel.tasks.observe(this, Observer { tasks ->
            adapter.updateList(tasks)
        })
        taskListViewModel.loadTasksByLectureId(arguments!!.getInt(LECTURE_ID_ARG))
    }



    companion object {

        private const val LECTURE_ID_ARG = "lecture_id_arg"
        private const val TITLE_ARG = "title_arg"

        fun newInstance(lectureId: Int, title: String): TaskListFragment {
            val args = Bundle()
            args.putInt(LECTURE_ID_ARG, lectureId)
            args.putString(TITLE_ARG, title)

            val fragment = TaskListFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
