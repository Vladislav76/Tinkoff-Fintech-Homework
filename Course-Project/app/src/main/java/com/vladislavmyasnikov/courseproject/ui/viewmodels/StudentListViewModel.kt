package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.Student
import java.util.*

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    var recentRequestTime: Long = 0

    private val mDataRepository: DataRepository = DataRepository.getInstance(application)
    val students: LiveData<List<StudentEntity>> = mDataRepository.loadStudents()

    fun updateStudents(students: List<Student>) {
        Thread {
            mDataRepository.insertStudents(convertStudentsToEntities(students))
        }.start()
    }

    companion object {

        fun convertStudentsToEntities(students: List<Student>): List<StudentEntity> {
            val entities = ArrayList<StudentEntity>()
            for (student in students) {
                entities.add(StudentEntity(student.id, student.name, student.grades?.lastOrNull()!!.mark))
            }
            return entities
        }
    }
}