package com.vladislavmyasnikov.courseproject.ui.main.interfaces

interface UpdateStartListener {

    fun startUpdate()
}

interface UpdateStopListener {

    fun stopUpdate(message: String = "")
}