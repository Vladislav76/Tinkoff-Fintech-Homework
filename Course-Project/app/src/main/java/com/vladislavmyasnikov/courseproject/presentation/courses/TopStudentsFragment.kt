package com.vladislavmyasnikov.courseproject.presentation.courses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerAcademicPerformanceFragmentInjector
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.presentation.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.StudentListViewModelFactory
import kotlinx.android.synthetic.main.fragment_top_students.*
import javax.inject.Inject

class TopStudentsFragment : GeneralFragment() {

    @Inject
    lateinit var studentListVMFactory: StudentListViewModelFactory

    @Inject
    lateinit var adapter: StudentAdapter

    private lateinit var studentListVM: StudentListViewModel

    private val onTitleClickListener = View.OnClickListener {
        val fragment = StudentListFragment.newInstance()
        fragmentController?.addFragmentOnTop(fragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerAcademicPerformanceFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectAcademicPerformanceFragment(this)
        studentListVM = ViewModelProviders.of(this, studentListVMFactory).get(StudentListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_top_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setOnClickListener(onTitleClickListener)
        adapter.viewType = StudentAdapter.ViewType.COMPACT_VIEW
        recycler_view.adapter = adapter
    }

    fun updateContent(content: List<Student>) {
        if (content.isNotEmpty()) {
            recycler_view.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
            adapter.setAndSortListByStudentPointsAndName(content.take(10))
        }
    }

    companion object {
        fun newInstance(): TopStudentsFragment {
            return TopStudentsFragment()
        }
    }
}
