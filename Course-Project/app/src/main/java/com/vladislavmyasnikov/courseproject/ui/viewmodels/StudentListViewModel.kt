package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.db.entities.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.repositories_impl.StudentRepository
import javax.inject.Inject

class StudentListViewModel(private val studentRepository: StudentRepository) : ViewModel() {

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



class StudentListViewModelFactory @Inject constructor(private val studentRepository: StudentRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StudentListViewModel::class.java)) {
            StudentListViewModel(studentRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}