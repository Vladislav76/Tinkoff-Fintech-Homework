package com.vladislavmyasnikov.courseproject.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.vladislavmyasnikov.courseproject.data.DataRepository
import com.vladislavmyasnikov.courseproject.data.db.entity.StudentEntity
import com.vladislavmyasnikov.courseproject.data.models.Student
import com.vladislavmyasnikov.courseproject.data.network.Students
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val mApplication = application
    private val mDataRepository: DataRepository = DataRepository.getInstance(application)

    val students: LiveData<List<StudentEntity>> = mDataRepository.loadStudents()
    var recentRequestTime: Long = 0

    fun updateStudents() {
        if (System.currentTimeMillis() - recentRequestTime > CASH_LIFE_TIME) {
            val preferences = mApplication.getSharedPreferences(AuthorizationActivity.COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
            val token = preferences.getString(AuthorizationActivity.AUTHORIZATION_TOKEN, null) ?: ""

            mDataRepository.loadStudents(token, object : Callback<List<Students>> {
                override fun onFailure(call: Call<List<Students>>, e: Throwable) {
                    recentRequestTime = System.currentTimeMillis()
                }

                override fun onResponse(call: Call<List<Students>>, response: Response<List<Students>>) {
                    val result = response.body()
                    if (response.message() == "OK" && result != null && result.isNotEmpty()) {
                        Thread {
                            mDataRepository.insertStudents(convertStudentsToEntities(result[1].students))
                        }.start()
                    }
                    recentRequestTime = System.currentTimeMillis()
                }
            })
            println("fetch from server..")
        } else {
        }
    }



    companion object {

        private const val CASH_LIFE_TIME = 10_000

        fun convertStudentsToEntities(students: List<Student>): List<StudentEntity> {
            val entities = ArrayList<StudentEntity>()
            for (student in students) {
                entities.add(StudentEntity(student.id, student.name, student.grades?.lastOrNull()!!.mark))
            }
            return entities
        }
    }
}