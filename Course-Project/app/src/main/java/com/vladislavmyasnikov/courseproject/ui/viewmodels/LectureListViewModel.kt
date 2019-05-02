package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class LectureListViewModel(private val lectureRepository: ILectureRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val lectureEmitter = BehaviorSubject.create<List<Lecture>>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val lectures: Observable<List<Lecture>> = lectureEmitter
    val errors: Observable<Throwable> = errorEmitter

    fun fetchLectures() {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(lectureRepository.fetchLectures()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        progressEmitter.onNext(false)
                        isLoading = false
                    }
                    .subscribe({ lectures ->
                        lectureEmitter.onNext(lectures)
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
class LectureListViewModelFactory @Inject constructor(private val lectureRepository: ILectureRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LectureListViewModel::class.java)) {
            LectureListViewModel(lectureRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
