package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class LectureAdapter(private val mCallback: OnItemClickCallback) : RecyclerView.Adapter<LectureAdapter.LectureViewHolder>() {

    private var mLectures: List<LectureEntity>? = null

    fun updateList(lectures: List<LectureEntity>) {
        if (mLectures == null) {
            mLectures = lectures
            notifyItemRangeInserted(0, lectures.size)
        } else {
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(mLectures, lectures))
            mLectures = lectures
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_lecture, parent, false)
        return LectureViewHolder(view, mCallback)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(mLectures!![position])
    }

    override fun getItemCount(): Int {
        return if (mLectures == null) 0 else mLectures!!.size
    }

    internal class LectureViewHolder(view: View, private val mCallback: OnItemClickCallback) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val mNameView: TextView
        private var mLecture: LectureEntity? = null

        init {
            mNameView = view.findViewById(R.id.name_field)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mCallback.onClick(mLecture!!.id, mLecture!!.title)
        }

        fun bind(lecture: LectureEntity) {
            mLecture = lecture
            mNameView.text = lecture.title
        }
    }
}