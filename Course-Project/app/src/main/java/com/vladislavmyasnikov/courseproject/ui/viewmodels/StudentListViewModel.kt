package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class StudentListViewModel(
        private val studentRepository: IStudentRepository,
        private val profileRepository: IProfileRepository
) : ViewModel() {

    private var isStudentsLoading = false
    private var isProfileLoading = false
    private var loadedStudents: List<Student>? = null
    private var loadedProfile: Profile? = null

    private val disposables = CompositeDisposable()
    private val loadingState = PublishSubject.create<Outcome<List<Student>>>()
    private var wasError = false

    val studentsFetchOutcome: Observable<Outcome<List<Student>>> = loadingState
    var students: List<Student> = emptyList()
    var isLoading: Boolean = false

    init {
        disposables.add(studentRepository.studentsFetchOutcome
                .subscribeOn(Schedulers.io())
                .map {
                    if (it is Outcome.Success) Outcome.success(it.data.filter { student -> student.mark >= 20.0 })
                    else it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    println(Thread.currentThread())
                    when (it) {
                        is Outcome.Success -> {
                            loadedStudents = it.data
                            updateStudents()
                            Log.d("STUDENT_LIST_VM", "Students are fetched (size: ${it.data.size})")
                        }
                        is Outcome.Progress -> {
                            isStudentsLoading = it.loading
                            updateLoading()
                        }
                        is Outcome.Failure -> {
                            error(it.e)
                        }
                    }
                })

        disposables.add(profileRepository.profileFetchOutcome
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    println(Thread.currentThread())
                    when (it) {
                        is Outcome.Success -> {
                            loadedProfile = it.data
                            updateStudents()
                            Log.d("STUDENT_LIST_VM", "Profile is fetched")
                        }
                        is Outcome.Progress -> {
                            isProfileLoading = it.loading
                            updateLoading()
                        }
                        is Outcome.Failure -> {
                            error(it.e)
                        }
                    }
                })
    }

    fun fetchStudents() {
        if (!isProfileLoading && !isStudentsLoading) {
            wasError = false
            studentRepository.fetchStudents()
            profileRepository.fetchProfile()
        }
    }

    fun refreshStudents() {
        if (!isProfileLoading && !isStudentsLoading) {
            wasError = false
            studentRepository.refreshStudents()
            profileRepository.refreshProfile()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun updateLoading() {
        isLoading = isStudentsLoading || isProfileLoading
        loadingState.onNext(Outcome.loading(isLoading))
    }

    private fun updateStudents() {
        val profile = loadedProfile
        val _students = loadedStudents
        if (profile != null && _students != null) {
            val processingStudents = _students.map { if (it.id == profile.id) it.copy(name = "Вы") else it }
            loadingState.onNext(Outcome.success(processingStudents))
            students = processingStudents
        }
    }

    private fun error(e: Throwable) {
        if (!wasError) {
            wasError = true
            loadingState.onNext(Outcome.failure(e))
        }
    }
}

/*
 * Factory class
 */
class StudentListViewModelFactory @Inject constructor(private val studentRepository: IStudentRepository, private val profileRepository: IProfileRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StudentListViewModel::class.java)) {
            StudentListViewModel(studentRepository, profileRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}