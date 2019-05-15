package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
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
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStartListener
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStopListener
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModelFactory
import javax.inject.Inject

class AcademicPerformanceFragment : Fragment(), UpdateStartListener {

    @Inject
    lateinit var studentListViewModelFactory: StudentListViewModelFactory

    @Inject
    lateinit var mAdapter: StudentAdapter

    private lateinit var mStudentListViewModel: StudentListViewModel

    private var mUpdateStopListener: UpdateStopListener? = null

    private val mOnTitleListener = View.OnClickListener {
        val intent = Intent(context, StudentListActivity::class.java)
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mUpdateStopListener = when {
            context is UpdateStopListener -> context
            parentFragment is UpdateStopListener -> parentFragment as UpdateStopListener
            else -> throw IllegalStateException("Context or parent fragment must implement UpdateStopListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.title).setOnClickListener(mOnTitleListener)

        val injector = DaggerAcademicPerformanceFragmentInjector.builder().appComponent(App.appComponent).contextModule(ContextModule(activity!!)).build()
        injector.injectAcademicPerformanceFragment(this)
        mStudentListViewModel = ViewModelProviders.of(this, studentListViewModelFactory).get(StudentListViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        mAdapter.viewType = StudentAdapter.ViewType.COMPACT_VIEW
        recyclerView.adapter = mAdapter
    }

    override fun startUpdate() {
        mStudentListViewModel.fetchStudents()
    }

    override fun onDetach() {
        super.onDetach()
        mUpdateStopListener = null
    }



    companion object {

        fun newInstance(): AcademicPerformanceFragment {
            return AcademicPerformanceFragment()
        }
    }
}
