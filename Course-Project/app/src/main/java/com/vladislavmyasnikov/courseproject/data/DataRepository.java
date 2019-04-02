package com.vladislavmyasnikov.courseproject.data;

import android.app.Application;

import com.vladislavmyasnikov.courseproject.data.db.LocalDatabase;
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class DataRepository {

    private static DataRepository sInstance;
    private static final String LOCAL_DATABASE_NAME = "local_database";

    private LocalDatabase mLocalDatabase;
    private Executor mExecutor;

    private DataRepository(Application application) {
        mLocalDatabase = Room.databaseBuilder(application, LocalDatabase.class, LOCAL_DATABASE_NAME).build();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<LectureEntity>> loadLectures() {
        return mLocalDatabase.lectureDao().loadLectures();
    }

    public LiveData<List<TaskEntity>> loadTasks(int lectureId) {
        return mLocalDatabase.taskDao().loadTasks(lectureId);
    }

    public void insertLectures(final List<LectureEntity> lectures) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mLocalDatabase.lectureDao().insertLectures(lectures);
            }
        });
    }

    public void insertTasks(final List<TaskEntity> tasks) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mLocalDatabase.taskDao().insertTasks(tasks);
            }
        });
    }

    public static DataRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(application);
                }
            }
        }
        return sInstance;
    }
}
