package com.vladislavmyasnikov.courseproject.ui.viewmodels;

import android.app.Application;

import com.vladislavmyasnikov.courseproject.data.DataRepository;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class TaskListViewModel extends AndroidViewModel {

    private DataRepository mDataRepository;
    private MediatorLiveData<List<TaskEntity>> mTasks = new MediatorLiveData<>();

    public TaskListViewModel(Application application) {
        super(application);
        mDataRepository = DataRepository.Companion.getInstance(application);
    }

    public void init(int lectureId) {
        mTasks.addSource(mDataRepository.loadTasks(lectureId), new Observer<List<TaskEntity>>() {
            @Override
            public void onChanged(List<TaskEntity> tasks) {
                mTasks.postValue(tasks);
            }
        });
    }

    public LiveData<List<TaskEntity>> getTasks() {
        return mTasks;
    }
}
