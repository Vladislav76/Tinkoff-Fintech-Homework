package com.vladislavmyasnikov.courseproject.ui.courses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.ui.adapters.StudentAdapter
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStartListener
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.UpdateStopListener
import com.vladislavmyasnikov.courseproject.ui.viewmodels.StudentListViewModel

class AcademicPerformanceFragment : Fragment(), UpdateStartListener {

    private val mStudentListViewModel: StudentListViewModel by lazy {
        ViewModelProviders.of(this).get(StudentListViewModel::class.java)
    }
    private lateinit var mAdapter: StudentAdapter
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        mAdapter = StudentAdapter(activity!!, StudentAdapter.ViewType.COMPACT_VIEW)
        recyclerView.adapter = mAdapter

        mStudentListViewModel.students.observe(this, Observer { students ->
            val topStudents = students.sortedBy { -it.mark }.take(10)
            mAdapter.setStudentList(topStudents)
            mAdapter.updateList(topStudents)
        })

        mStudentListViewModel.responseMessage.observe(this, Observer {
            if (it != null) {
                when (it) {
                    ResponseMessage.SUCCESS -> mUpdateStopListener?.stopUpdate("success")
                    ResponseMessage.LOADING -> {}
                    ResponseMessage.NO_INTERNET -> {
                        Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        mUpdateStopListener?.stopUpdate("no internet")
                    }
                    ResponseMessage.ERROR -> mUpdateStopListener?.stopUpdate("fail")
                }
            }
        })
    }

    override fun startUpdate() {
        mStudentListViewModel.updateStudents()
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
