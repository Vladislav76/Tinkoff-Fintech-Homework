package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback

import java.text.DateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var mTasks: List<TaskEntity>? = null

    fun updateList(tasks: List<TaskEntity>) {
        if (mTasks == null) {
            mTasks = tasks
            notifyItemRangeInserted(0, tasks.size)
        } else {
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(mTasks, tasks))
            mTasks = tasks
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(mTasks!![position])
    }

    override fun getItemCount(): Int {
        return if (mTasks == null) 0 else mTasks!!.size
    }

    internal class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mNameView: TextView
        private val mDeadlineView: TextView
        private val mStatusView: TextView
        private val mMarkView: TextView

        init {
            mNameView = view.findViewById(R.id.name_field)
            mDeadlineView = view.findViewById(R.id.deadline_field)
            mStatusView = view.findViewById(R.id.status_field)
            mMarkView = view.findViewById(R.id.mark_field)
        }

        fun bind(task: TaskEntity) {
            mNameView.text = task.title
            mStatusView.text = task.status
            mMarkView.text = String.format(Locale.getDefault(), "%.2f/%.2f", task.mark, task.maxScore)

            val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US)
            val date = task.deadline
            if (date != null) {
                mDeadlineView.text = formatter.format(date)
            }
        }
    }
}