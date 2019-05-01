package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerAcademicPerformanceFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModelFactory
import javax.inject.Inject

class TopStudentsFragment : Fragment() {

    @Inject
    lateinit var studentListViewModelFactory: StudentListViewModelFactory

    @Inject
    lateinit var adapter: StudentAdapter

    private lateinit var studentListViewModel: StudentListViewModel

    private val onTitleClickListener = View.OnClickListener {
        val intent = Intent(context, StudentListActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.title).setOnClickListener(onTitleClickListener)

        val injector = DaggerAcademicPerformanceFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectAcademicPerformanceFragment(this)
        studentListViewModel = ViewModelProviders.of(this, studentListViewModelFactory).get(StudentListViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        adapter.viewType = StudentAdapter.ViewType.COMPACT_VIEW
        recyclerView.adapter = adapter
    }

    fun updateContent(content: List<Student>) {
        adapter.setAndSortListByStudentPointsAndName(content.take(10))
    }

    companion object {
        fun newInstance(): TopStudentsFragment {
            return TopStudentsFragment()
        }
    }
}