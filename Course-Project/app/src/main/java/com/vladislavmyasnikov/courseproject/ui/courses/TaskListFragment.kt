package com.vladislavmyasnikov.courseproject.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.ui.adapters.TaskAdapter
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.TaskListViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListFragment : GeneralFragment() {

    private var mTaskListViewModel: TaskListViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view

        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener!!.setToolbarTitle(arguments!!.getString(TITLE_ARG)!!)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = TaskAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mTaskListViewModel = ViewModelProviders.of(this).get(TaskListViewModel::class.java)
        mTaskListViewModel!!.tasks.observe(this, Observer { tasks ->
            if (tasks != null) {
                adapter.updateList(tasks)
            }
        })
        mTaskListViewModel!!.init(arguments!!.getInt(LECTURE_ID_ARG))
    }

    companion object {

        private val LECTURE_ID_ARG = "lecture_id_arg"
        private val TITLE_ARG = "title_arg"

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
