package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureEntity
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback

class LectureAdapter : RecyclerView.Adapter<LectureAdapter.LectureViewHolder>() {

    var callback: OnItemClickCallback? = null
    private var mLectures: List<Lecture> = emptyList()

    fun updateList(lectures: List<Lecture>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(mLectures, lectures))
        mLectures = lectures
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_lecture, parent, false)
        val holder = LectureViewHolder(view)

        holder.itemView.setOnClickListener {
            val lecture = mLectures[holder.adapterPosition]
            callback?.onClick(lecture.id, lecture.title)
        }
        return holder
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(mLectures[position])
    }

    override fun getItemCount(): Int = mLectures.size



    class LectureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mNameView = view.findViewById<TextView>(R.id.name_field)

        fun bind(lecture: Lecture) {
            mNameView.text = lecture.title
        }
    }
}