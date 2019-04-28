package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureEntity
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories_impl.LectureRepository
import javax.inject.Inject

class LectureListViewModel(private val lectureRepository: LectureRepository) : ViewModel() {

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



class LectureListViewModelFactory @Inject constructor(private val lectureRepository: LectureRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LectureListViewModel::class.java)) {
            LectureListViewModel(lectureRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
