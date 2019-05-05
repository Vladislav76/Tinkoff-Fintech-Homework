package com.vladislavmyasnikov.courseproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vladislavmyasnikov.courseproject.data.mapper.ExceptionMapper
import com.vladislavmyasnikov.courseproject.domain.entities.*
import com.vladislavmyasnikov.courseproject.domain.repositories.ICourseRepository
import com.vladislavmyasnikov.courseproject.domain.repositories.ILectureRepository
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

class CourseViewModel(
        private val courseRepository: ICourseRepository,
        private val profileRepository: IProfileRepository,
        private val studentRepository: IStudentRepository,
        private val lectureRepository: ILectureRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val progressEmitter = BehaviorSubject.create<Boolean>()
    private val studentEmitter = BehaviorSubject.create<List<Student>>()
    private val courseEmitter = BehaviorSubject.create<Course>()
    private val errorEmitter = PublishSubject.create<Throwable>()
    private var isLoading = false

    val loadingState: Observable<Boolean> = progressEmitter
    val students: Observable<List<Student>> = studentEmitter
    val course: Observable<Course> = courseEmitter
    val errors: Observable<Throwable> = errorEmitter

    fun fetchCourse() {
        if (!isLoading) {
            isLoading = true
            progressEmitter.onNext(true)
            disposables.add(
                    courseRepository.fetchCourse()
                            .concatWith(Observable.zip(
                                    courseRepository.fetchCourseUrlAndTitle(),
                                    profileRepository.fetchProfile(),
                                    BiFunction<Pair<String,String>, Profile, Triple<String,String,Int>> {
                                        (url,title), profile -> Triple(url, title, profile.id)
                                    }
                            ).concatMap { (url,title,id) ->
                                Observable.zip(
                                        studentRepository.fetchStudents()
                                                .map { students ->
                                                    students.map { if (it.id == id) it.copy(name = "Вы") else it }
                                                            .sortedWith(StudentByPointsAndNameComparator)
                                                }
                                                .doAfterNext { studentEmitter.onNext(it) },
                                        lectureRepository.fetchLectures(),
                                        BiFunction<List<Student>, List<Lecture>, Course> { students, lectures ->
                                            extractCourseData(url, title, id, students, lectures)
                                        }
                                ).doAfterNext { course ->
                                    courseRepository.saveCourse(course)
                                }
                            }
                            ).doFinally {
                                progressEmitter.onNext(false)
                                isLoading = false
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ course ->
                                courseEmitter.onNext(course)
                            }, { error ->
                                errorEmitter.onNext(ExceptionMapper.map(error))
                            })
            )
        }
    }

    private fun extractCourseData(url: String, title: String, profileId: Int, students: List<Student>, lectures: List<Lecture>): Course {
        /* Extract from students */
        val studentCount = students.size
        val profile = students.find { it.id == profileId }
        val points = profile?.mark ?: 0.0f
        val ratingPosition = students.indexOf(profile) + 1

        //TODO: maybe from lectures???  LectureWithTasks -> Data + List<Task>; LectureRepo: fetchLectures() & fetchLecturesWithTasks()
        var testCount = 0
        var homeworkCount = 0
        var okTestCount = 0
        var okHomeworkCount = 0
        profile?.marks?.forEach {
            when (it.taskType) {
                "test_during_lecture" -> {
                    testCount++
                    if (it.status == "accepted") okTestCount++
                }
                "full" -> {
                    homeworkCount++
                    if (it.status == "accepted") okHomeworkCount++
                }
            }
        }

        /* Extract from lectures */
        val lectureCount = lectures.size
        val pastLectureCount = homeworkCount
        val remainingLectureCount = lectureCount - pastLectureCount

        return Course(url, title, points, ratingPosition, studentCount, okTestCount, testCount, okHomeworkCount, homeworkCount,
                pastLectureCount, remainingLectureCount, lectureCount)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

/*
 * Factory class
 */
class CourseViewModelFactory @Inject constructor(
        private val courseRepository: ICourseRepository,
        private val profileRepository: IProfileRepository,
        private val studentRepository: IStudentRepository,
        private val lectureRepository: ILectureRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            CourseViewModel(courseRepository, profileRepository, studentRepository, lectureRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}