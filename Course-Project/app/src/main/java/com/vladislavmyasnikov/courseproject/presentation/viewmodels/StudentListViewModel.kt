package com.vladislavmyasnikov.courseproject.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.entities.Student
import com.vladislavmyasnikov.courseproject.domain.entities.StudentByPointsAndNameComparator
import com.vladislavmyasnikov.courseproject.domain.repositories.IProfileRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.IStudentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class StudentListViewModel(
        private val studentRepository: IStudentRepository,
        private val profileRepository: IProfileRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val studentEmitter = BehaviorSubject.create<List<Student>>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val students: Observable<List<Student>> = studentEmitter
    val errors: Observable<Throwable> = errorEmitter

    fun fetchStudents() {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(
                    Observable.zip(
                            profileRepository.fetchProfile().subscribeOn(Schedulers.io()),
                            studentRepository.fetchStudents().subscribeOn(Schedulers.io()),
                            BiFunction<Profile, List<Student>, List<Student>> { profile, students ->
                                students.map { if (profile.id == it.id) it.copy(name = "Вы") else it }
                                        .sortedWith(StudentByPointsAndNameComparator)
                            })
                            .doFinally {
                                progressEmitter.onNext(false)
                                isLoading = false
                            }
                            .subscribe({ students ->
                                studentEmitter.onNext(students)
                            }, { error ->
                                errorEmitter.onNext(ExceptionMapper.map(error))
                            })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
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