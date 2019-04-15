package com.vladislavmyasnikov.courseproject.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.ui.components.InitialsRoundView
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback

class StudentAdapter(val context: Context) : RecyclerView.Adapter<StudentAdapter.GridUserViewHolder>(), Filterable {

    private var mStudents: List<StudentEntity> = listOf()
    private var mSourceStudents: List<StudentEntity> = listOf()
    private var mViewType: Int = LINEAR_USER_VIEW

    private val filter = object : Filter() {
        override fun performFiltering(query: CharSequence): FilterResults {
            val students = if (query.isEmpty()) mSourceStudents else mSourceStudents.filter { it.name.contains(query, ignoreCase = true) }
            return FilterResults().also { it.values = students }
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            val students = filterResults.values as List<StudentEntity>
            sortByPoints(students)
        }
    }

    fun updateList(students: List<StudentEntity>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(mStudents, students))
        mStudents = students
        diffResult.dispatchUpdatesTo(this)
    }

    fun setStudentList(students: List<StudentEntity>) {
        mSourceStudents = students
    }

    fun sortByDefault() {
        updateList(mSourceStudents)
    }

    fun sortByName(students: List<StudentEntity>? = null) {
        if (students == null) {
            updateList(mStudents.sortedBy { it.name })
        } else {
            updateList(students.sortedBy { it.name })
        }
    }

    fun sortByPoints(students: List<StudentEntity>? = null) {
        if (students == null) {
            updateList(mStudents.sortedWith(compareBy({ -it.mark }, { it.name })))
        } else {
            updateList(students.sortedWith(compareBy({ -it.mark }, { it.name })))
        }
    }

    fun setViewType(viewType: Int) {
        mViewType = viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridUserViewHolder =
            if (viewType == LINEAR_USER_VIEW) {
                LinearUserViewHolder(createView(parent, viewType))
            } else {
                GridUserViewHolder(createView(parent, viewType))
            }

    override fun onBindViewHolder(holder: GridUserViewHolder, position: Int) {
        holder.bind(mStudents[position], context)
    }

    override fun getItemCount(): Int = mStudents.size

    override fun getItemViewType(position: Int): Int = mViewType

    override fun getFilter(): Filter = filter


    private fun createView(parent: ViewGroup, viewType: Int): View {
        val inflater = LayoutInflater.from(parent.context)
        val layoutItemId: Int =
                when (viewType) {
                    LINEAR_USER_VIEW -> R.layout.item_linear_user
                    else -> R.layout.item_grid_user
                }
        return inflater.inflate(layoutItemId, parent, false)
    }



    open class GridUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mNameView = view.findViewById<TextView>(R.id.user_name_field)
        private val mIconView = view.findViewById<InitialsRoundView>(R.id.user_icon)

        open fun bind(student: StudentEntity, context: Context) {
            val initials: String = student.name.split(" ").map { it[0] }.joinToString(separator = "")
            mNameView.text = student.name
            mIconView.setText(initials)
            mIconView.setIconColor(getColor(student.name))
        }
    }



    class LinearUserViewHolder(view: View) : GridUserViewHolder(view) {
        private val mPointsView = view.findViewById<TextView>(R.id.user_points_field)

        override fun bind(student: StudentEntity, context: Context) {
            super.bind(student, context)
            val points = context.resources.getQuantityString(R.plurals.numberOfPoints, Math.floor(student.mark).toInt(), student.mark)
            mPointsView.text = points.toString()
        }
    }

    companion object {

        val LINEAR_USER_VIEW = 1
        val GRID_USER_VIEW = 2

        fun getColor(displayName: String?): Int {
            val values = intArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF)
            var color = 0x00000000
            if (displayName != null && displayName != "") {
                val hash = Math.abs(displayName.hashCode())
                for (i in 1..6) {
                    color = color shl 4
                    color = color or values[hash / (11 * i) % 16]
                }
            }
            return color or -0x1000000
        }
    }
}
