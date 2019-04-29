package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class StudentListViewModel(private val studentRepository: IStudentRepository) : ViewModel() {

    val studentsFetchOutcome: Observable<Outcome<List<Student>>> = studentRepository.studentsFetchOutcome
    var students: List<Student> = emptyList()
    var isLoading: Boolean = false
    private val disposables = CompositeDisposable()

    init {
        disposables.add(studentsFetchOutcome.subscribe {
            when (it) {
                is Outcome.Success -> {
                    students = it.data
                    Log.d("STUDENT_LIST_VM", "Student are fetched (size: ${students.size})")
                }
                is Outcome.Progress -> isLoading = it.loading
            }
        })
    }

    fun fetchStudents() {
        studentRepository.fetchStudents()
    }

    fun refreshStudents() {
        studentRepository.refreshStudents()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}



class StudentListViewModelFactory @Inject constructor(private val studentRepository: IStudentRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StudentListViewModel::class.java)) {
            StudentListViewModel(studentRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}