package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entities.TaskEntity
import com.vladislavmyasnikov.courseproject.domain.entities.EventTypeColor
import com.vladislavmyasnikov.courseproject.domain.entities.Task
import com.vladislavmyasnikov.courseproject.domain.entities.TaskStatus
import com.vladislavmyasnikov.courseproject.domain.entities.TaskType
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var mTasks: List<Task> = emptyList()

    fun updateList(tasks: List<Task>) {
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
        private val title = view.findViewById<TextView>(R.id.task_title)
        private val deadline = view.findViewById<TextView>(R.id.task_deadline)
        private val status = view.findViewById<TextView>(R.id.task_status)
        private val mark = view.findViewById<TextView>(R.id.task_mark)
        private val icon = view.findViewById<ImageView>(R.id.task_icon)

        fun bind(item: Task) {
            title.text = item.title
            mark.text = String.format(Locale.getDefault(), "%.2f/%.2f", item.mark, item.maxScore)

            val statusText = when (item.status) {
                TaskStatus.ON_CHECK -> "Проверка"
                TaskStatus.NEW -> "Новое"
                TaskStatus.ACCEPTED -> "OK"
                TaskStatus.OTHER -> ""
            }
            status.text = statusText

            val format = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault())
            if (item.deadline != null) {
                deadline.text = format.format(item.deadline)
            }

            val iconRes = when (item.taskType) {
                TaskType.TEST -> R.drawable.test_icon
                TaskType.HOMEWORK -> R.drawable.homework_icon
                TaskType.OTHER -> R.drawable.group_9
            }
            icon.setImageResource(iconRes)
        }
    }
}