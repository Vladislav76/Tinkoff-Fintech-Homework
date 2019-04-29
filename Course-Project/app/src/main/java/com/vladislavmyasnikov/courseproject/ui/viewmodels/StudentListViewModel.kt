package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.repositories_impl.ProfileRepository_Factory
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StudentListViewModel(
        private val studentRepository: IStudentRepository,
        private val profileRepository: IProfileRepository
) : ViewModel() {

    val studentsFetchOutcome: Observable<Outcome<List<Student>>>
    var students: List<Student> = emptyList()
    var isLoading: Boolean = false
    private val disposables = CompositeDisposable()

    init {
        studentsFetchOutcome = Observable.zip(
                studentRepository.studentsFetchOutcome.map {
                    if (it is Outcome.Success)
                        Outcome.success(it.data.filter { student -> student.mark >= 20.0 })
                    else it
                },
                profileRepository.profileFetchOutcome,
                BiFunction<Outcome<List<Student>>, Outcome<Profile>, Outcome<List<Student>>> {
                    outcome1, outcome2 ->
                    if (outcome1 is Outcome.Success && outcome2 is Outcome.Success) {
                        Outcome.success(
                                outcome1.data.map {
                                    if (it.id == outcome2.data.id)
                                        it.copy(it.id, "Вы", it.mark)
                                    else it
                                })
                    } else {
                        outcome1
                    }
                }
        )

        disposables.add(studentsFetchOutcome
                .subscribeOn(Schedulers.io())
                .subscribe {
                     println(it.toString())
                     when (it) {
                        is Outcome.Success -> {
                            students = it.data
                            Log.d("STUDENT_LIST_VM", "Student are fetched (size: ${students.size})")
                        }
                        is Outcome.Progress -> isLoading = it.loading
                    }
                }
        )
    }

    fun fetchStudents() {
        studentRepository.fetchStudents()
        profileRepository.fetchProfile()
    }

    fun refreshStudents() {
        studentRepository.refreshStudents()
        profileRepository.refreshProfile()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}



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