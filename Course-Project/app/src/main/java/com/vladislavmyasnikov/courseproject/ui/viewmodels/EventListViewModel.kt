package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.entities.Event
import com.vladislavmyasnikov.courseproject.domain.repositories.IEventRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class EventListViewModel(private val repository: IEventRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val eventEmitter = BehaviorSubject.create<List<Event>>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val events: Observable<List<Event>> = eventEmitter
    val errors: Observable<Throwable> = errorEmitter

    fun fetchEvents() {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(repository.fetchEvents()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        progressEmitter.onNext(false)
                        isLoading = false
                    }
                    .subscribe({ lectures ->
                        eventEmitter.onNext(lectures)
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
class EventListViewModelFactory @Inject constructor(private val repository: IEventRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EventListViewModel::class.java)) {
            EventListViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
