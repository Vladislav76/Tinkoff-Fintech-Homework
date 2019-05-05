package com.vladislavmyasnikov.courseproject.presentation.courses

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
import com.vladislavmyasnikov.courseproject.presentation.adapters.TaskAdapter
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.TaskListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.TaskListViewModelFactory
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
        actionBarController?.setDisplayHomeAsUpEnabled(true)
        actionBarController?.setTitle(arguments!!.getString(TITLE_ARG)!!)
        mainPanelController?.hideMainPanel()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        disposables.add(taskListVM.loadTasksByLectureId(arguments!!.getInt(LECTURE_ID_ARG))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tasks ->
                    adapter.updateList(tasks)
                }, { error ->
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                })
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
