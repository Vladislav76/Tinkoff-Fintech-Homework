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
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.entities.StudentByNameComparator
import com.vladislavmyasnikov.courseproject.domain.entities.StudentByPointsAndNameComparator
import com.vladislavmyasnikov.courseproject.ui.components.InitialsRoundView
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback

class StudentAdapter(private val context: Context) : RecyclerView.Adapter<StudentAdapter.ViewHolder>(), Filterable {

    var viewType: ViewType = ViewType.LINEAR_VIEW
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var students: List<Student> = emptyList()
    private var sourceStudents: List<Student> = emptyList()

    private val filter = object : Filter() {

        override fun performFiltering(query: CharSequence): FilterResults {
            val students = if (query.isEmpty()) sourceStudents else sourceStudents.filter { it.name.contains(query, ignoreCase = true) }
            return FilterResults().also { it.values = students }
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            val students = filterResults.values as List<Student>
            sortAndUpdateList(students)
        }
    }

    fun setSourceListWithoutUpdating() {
        students = sourceStudents
    }

    fun setListWithoutUpdating(_students: List<Student>) {
        sourceStudents = _students
    }

    fun sortListByStudentName() {
        updateList(students.sortedWith(StudentByNameComparator))
    }

    fun sortListByStudentPointsAndName() {
        updateList(students.sortedWith(StudentByPointsAndNameComparator))
    }

    fun setAndSortListByStudentName(_students: List<Student>) {
        sourceStudents = _students
        updateList(_students.sortedWith(StudentByNameComparator))
    }

    fun setAndSortListByStudentPointsAndName(_students: List<Student>) {
        sourceStudents = _students
        updateList(_students.sortedWith(StudentByPointsAndNameComparator))
    }

    private fun updateList(_students: List<Student>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(students, _students))
        students = _students
        diffResult.dispatchUpdatesTo(this)
    }

    private fun sortAndUpdateList(_students: List<Student>) {
        updateList(_students.sortedWith(StudentByPointsAndNameComparator))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewTypeOrdinal: Int): ViewHolder =
            when (ViewType.values()[viewTypeOrdinal]) {
                ViewType.LINEAR_VIEW, ViewType.COMPACT_VIEW -> ViewHolder(createView(parent, viewTypeOrdinal))
            }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(students[position], context)
    }

    override fun getItemCount(): Int = students.size

    override fun getItemViewType(position: Int): Int = viewType.ordinal

    override fun getFilter(): Filter = filter

    private fun createView(parent: ViewGroup, viewTypeOrdinal: Int): View {
        val inflater = LayoutInflater.from(parent.context)
        val layoutItemId = when (ViewType.values()[viewTypeOrdinal]) {
            ViewType.LINEAR_VIEW -> R.layout.item_linear_user
            ViewType.COMPACT_VIEW -> R.layout.item_compact_user
        }
        return inflater.inflate(layoutItemId, parent, false)
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mNameView = view.findViewById<TextView>(R.id.user_name_field)
        private val mPointsView = view.findViewById<TextView>(R.id.user_points_field)
        private val mIconView = view.findViewById<InitialsRoundView>(R.id.user_icon)

        open fun bind(student: Student, context: Context) {
            mNameView.text = student.name
            mIconView.setIconColor(getColor(student.name))
            val initials: String = student.name.split(" ").map { it[0] }.joinToString(separator = "")
            mIconView.setText(initials)
            val points = context.resources.getQuantityString(R.plurals.numberOfPoints, Math.floor(student.mark.toDouble()).toInt(), student.mark)
            mPointsView.text = points.toString()
        }
    }

    enum class ViewType {
        LINEAR_VIEW, COMPACT_VIEW
    }

    companion object {
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
