package com.vladislavmyasnikov.courseproject.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;
import com.vladislavmyasnikov.courseproject.ui.adapters.TaskAdapter;
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment;
import com.vladislavmyasnikov.courseproject.ui.viewmodels.TaskListViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListFragment extends GeneralFragment {

    private static final String LECTURE_ID_ARG = "lecture_id_arg";
    private static final String TITLE_ARG = "title_arg";

    private TaskListViewModel mTaskListViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(inflater.getContext());
        recyclerView.setId(R.id.recycler_view);

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(getArguments().getString(TITLE_ARG));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTaskListViewModel = ViewModelProviders.of(this).get(TaskListViewModel.class);
        mTaskListViewModel.getTasks().observe(this, new Observer<List<TaskEntity>>() {
            @Override
            public void onChanged(List<TaskEntity> tasks) {
                if (tasks != null) {
                    adapter.updateList(tasks);
                }
            }
        });
        mTaskListViewModel.init(getArguments().getInt(LECTURE_ID_ARG));
    }

    public static TaskListFragment newInstance(int lectureId, String title) {
        Bundle args = new Bundle();
        args.putInt(LECTURE_ID_ARG, lectureId);
        args.putString(TITLE_ARG, title);

        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
