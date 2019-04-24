package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories.StudentRepository
import com.vladislavmyasnikov.courseproject.di.components.DaggerDataSourceComponent
import com.vladislavmyasnikov.courseproject.di.modules.ContextModule

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val studentRepository: StudentRepository by lazy {
        val component = DaggerDataSourceComponent.builder().contextModule(ContextModule(application)).build()
        StudentRepository(component.getDatabase(), component.getNetworkService(), component.getMemory())
    }
    private val mutableResponseMessage = MutableLiveData<ResponseMessage>()
    val responseMessage: LiveData<ResponseMessage> = mutableResponseMessage
    val students: LiveData<List<StudentEntity>> = studentRepository.students

    fun updateStudents() {
        studentRepository.refreshStudents(object : StudentRepository.LoadStudentsCallback {
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