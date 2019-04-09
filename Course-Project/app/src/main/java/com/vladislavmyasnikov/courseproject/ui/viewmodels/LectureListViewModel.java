package com.vladislavmyasnikov.courseproject.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import com.vladislavmyasnikov.courseproject.data.DataRepository;
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;
import com.vladislavmyasnikov.courseproject.data.models.Lecture;
import com.vladislavmyasnikov.courseproject.data.models.Task;
import com.vladislavmyasnikov.courseproject.data.models.TaskInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class LectureListViewModel extends AndroidViewModel {

    private DataRepository mDataRepository;
    private MediatorLiveData<List<LectureEntity>> mLectures = new MediatorLiveData<>();

    public LectureListViewModel(Application application) {
        super(application);

        mDataRepository = DataRepository.Companion.getInstance(application);
        mLectures.addSource(mDataRepository.loadLectures(), new Observer<List<LectureEntity>>() {
            @Override
            public void onChanged(List<LectureEntity> lectureEntities) {
                mLectures.postValue(lectureEntities);
            }
        });
    }

    public LiveData<List<LectureEntity>> getLectures() {
        return mLectures;
    }

    public void updateLectures(List<Lecture> lectures) {
        mDataRepository.insertLectures(convertLecturesToEntities(lectures));
        for (Lecture lecture : lectures) {
            mDataRepository.insertTasks(convertTasksToEntities(lecture.tasks, lecture.id));
        }
    }

    private List<LectureEntity> convertLecturesToEntities(List<Lecture> lectures) {
        List<LectureEntity> entities = new ArrayList<>();
        for (Lecture lecture : lectures) {
            entities.add(new LectureEntity(lecture.id, lecture.title));
        }
        return entities;
    }

    private List<TaskEntity> convertTasksToEntities(List<TaskInfo> tasks, int lectureId) {
        List<TaskEntity> entities = new ArrayList<>();
        for (TaskInfo taskInfo : tasks) {
            Task task = taskInfo.task;
            Date date = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = task.deadlineDate == null ? null : format.parse(task.deadlineDate);
            }
            catch (ParseException e) {
                Log.d("LECTURE_LIST_VIEW_MODEL", "Incorrect string format for converting to the Date class");
            }
            finally {
                entities.add(new TaskEntity(taskInfo.id, task.title, taskInfo.status, taskInfo.mark, date, task.maxScore, lectureId));
            }
        }
        return entities;
    }
}
