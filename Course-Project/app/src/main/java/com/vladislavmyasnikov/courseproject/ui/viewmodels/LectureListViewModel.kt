package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories.LectureRepository

class LectureListViewModel(application: Application) : AndroidViewModel(application) {

    private val lectureRepository = LectureRepository.getInstance(application)
    private val mutableResponseMessage = MutableLiveData<ResponseMessage>()
    val responseMessage: LiveData<ResponseMessage> = mutableResponseMessage
    val lectures: LiveData<List<LectureEntity>> = lectureRepository.lectures

    fun updateLectures() {
        lectureRepository.refreshLectures(object : LectureRepository.LoadLecturesCallback {
            override fun onResponseReceived(response: ResponseMessage) {
                mutableResponseMessage.postValue(response)
                println("response: $response")
            }
        })
    }

    fun resetResponseMessage() {
        if (mutableResponseMessage.value != ResponseMessage.LOADING) {
            mutableResponseMessage.value = null
        }
    }
}
