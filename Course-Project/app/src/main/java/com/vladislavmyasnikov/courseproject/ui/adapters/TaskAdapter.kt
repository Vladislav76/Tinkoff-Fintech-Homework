package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var mTasks: List<TaskEntity> = emptyList()

    fun updateList(tasks: List<TaskEntity>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(mTasks, tasks))
        mTasks = tasks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(mTasks[position])
    }

    override fun getItemCount(): Int = mTasks.size



    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mNameView = view.findViewById<TextView>(R.id.name_field)
        private val mDeadlineView = view.findViewById<TextView>(R.id.deadline_field)
        private val mStatusView = view.findViewById<TextView>(R.id.status_field)
        private val mMarkView = view.findViewById<TextView>(R.id.mark_field)

        fun bind(task: TaskEntity) {
            mNameView.text = task.title
            mStatusView.text = task.status
            mMarkView.text = String.format(Locale.getDefault(), "%.2f/%.2f", task.mark, task.maxScore)

            val format = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault())
            if (task.deadline != null) {
                mDeadlineView.text = format.format(task.deadline)
            }
        }
    }
}