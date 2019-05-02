package com.vladislavmyasnikov.courseproject.utilities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<T : Identifiable<T>>(private val mOldList: List<T>, private val mNewList: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = mOldList.size

    override fun getNewListSize(): Int = mNewList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition].isIdentical(mNewList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            mOldList[oldItemPosition] == mNewList[newItemPosition]
}