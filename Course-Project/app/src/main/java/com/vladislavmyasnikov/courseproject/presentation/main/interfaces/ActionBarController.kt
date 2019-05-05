package com.vladislavmyasnikov.courseproject.presentation.main.interfaces

interface ActionBarController {

    fun setDisplayHomeAsUpEnabled(value: Boolean)
    fun setTitle(titleId: Int)
    fun setTitle(title: CharSequence)
}