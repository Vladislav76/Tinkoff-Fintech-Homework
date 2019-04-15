package com.vladislavmyasnikov.courseproject.utilities

import com.vladislavmyasnikov.courseproject.data.models.Identifiable

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback(private val mOldList: List<Identifiable>, private val mNewList: List<Identifiable>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = mOldList.size

    override fun getNewListSize(): Int = mNewList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition].id == mNewList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition] == mNewList[newItemPosition]
}