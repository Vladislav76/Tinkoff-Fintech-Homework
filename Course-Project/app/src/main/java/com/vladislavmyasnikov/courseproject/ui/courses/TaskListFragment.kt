package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerTaskListFragmentInjector
import com.vladislavmyasnikov.courseproject.ui.adapters.TaskAdapter
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.TaskListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.TaskListViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TaskListFragment : GeneralFragment() {

    @Inject
    lateinit var viewModelFactory: TaskListViewModelFactory

    @Inject
    lateinit var adapter: TaskAdapter

    private lateinit var taskListVM: TaskListViewModel
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerTaskListFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.injectTaskListFragment(this)
        taskListVM = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.id = R.id.recycler_view
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentListener?.setToolbarTitle(arguments!!.getString(TITLE_ARG)!!)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStart() {
        super.onStart()
        disposables.add(taskListVM.loadTasksByLectureId(arguments!!.getInt(LECTURE_ID_ARG))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tasks ->
                    adapter.updateList(tasks)
                }, { error ->
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                })
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        private const val LECTURE_ID_ARG = "lecture_id_arg"
        private const val TITLE_ARG = "title_arg"

        fun newInstance(lectureId: Int, title: String): TaskListFragment =
                TaskListFragment().apply {
                    arguments = Bundle().apply {
                        putString(TITLE_ARG, title)
                        putInt(LECTURE_ID_ARG, lectureId)
                    }
                }
    }
}
