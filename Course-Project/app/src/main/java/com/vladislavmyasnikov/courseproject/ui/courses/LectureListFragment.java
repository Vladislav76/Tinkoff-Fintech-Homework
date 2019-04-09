package com.vladislavmyasnikov.courseproject.ui.courses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;
import com.vladislavmyasnikov.courseproject.data.models.Result;
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback;
import com.vladislavmyasnikov.courseproject.data.network.RequestResultListener;
import com.vladislavmyasnikov.courseproject.data.network.RequestResultCallback;
import com.vladislavmyasnikov.courseproject.data.network.NetworkService;
import com.vladislavmyasnikov.courseproject.ui.adapters.LectureAdapter;
import com.vladislavmyasnikov.courseproject.ui.main.AuthorizationActivity;
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment;
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LectureListViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Response;

public class LectureListFragment extends GeneralFragment implements RequestResultListener<Result> {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LectureListViewModel mLectureListViewModel;
    private RequestResultCallback<Result> mRequestResultCallback = new RequestResultCallback<>(this);
    private OnItemClickCallback mItemClickCallback = new OnItemClickCallback() {
        @Override
        public void onClick(int id, String name) {
            TaskListFragment fragment = TaskListFragment.newInstance(id, name);
            mFragmentListener.addFragmentOnTop(fragment);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(inflater.getContext());
        recyclerView.setId(R.id.recycler_view);

        SwipeRefreshLayout layout = new SwipeRefreshLayout(inflater.getContext());
        layout.setId(R.id.swipe_refresh_layout);
        layout.addView(recyclerView);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.lectures_toolbar_title);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final LectureAdapter adapter = new LectureAdapter(mItemClickCallback);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mLectureListViewModel = ViewModelProviders.of(this).get(LectureListViewModel.class);
        mLectureListViewModel.getLectures().observe(this, new Observer<List<LectureEntity>>() {
            @Override
            public void onChanged(List<LectureEntity> lectures) {
                if (lectures != null && lectures.size() > 0) {
                    adapter.updateList(lectures);
                } else {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshData();
                }
            }
        });
    }

    @Override
    public void onFailure(Call<Result> call, Throwable e) {
        Toast.makeText(getActivity(), R.string.not_ok_status_message, Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResponse(Call<Result> call, Response<Result> response) {
        Result result = response.body();
        if (response.message().equals("OK") && result != null && result.getLectures() != null) {
            mLectureListViewModel.updateLectures(result.getLectures());
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestResultCallback.setRequestResultListener(null);
    }

    private void refreshData() {
        SharedPreferences preferences = getActivity().getSharedPreferences(AuthorizationActivity.Companion.getCOOKIES_STORAGE_NAME(), Context.MODE_PRIVATE);
        String token = preferences.getString(AuthorizationActivity.Companion.getAUTHORIZATION_TOKEN(), null);
        NetworkService.Companion.getInstance().getFintechService().getLectures(token).enqueue(mRequestResultCallback);
    }

    public static LectureListFragment newInstance() {
        return new LectureListFragment();
    }
}
