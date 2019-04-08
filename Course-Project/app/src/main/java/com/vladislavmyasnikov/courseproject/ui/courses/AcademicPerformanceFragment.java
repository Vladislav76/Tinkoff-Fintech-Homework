package com.vladislavmyasnikov.courseproject.ui.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.utilities.DataUpdater;
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnRefreshLayoutListener;
import com.vladislavmyasnikov.courseproject.ui.components.UserView;

public class AcademicPerformanceFragment extends Fragment {

    private static final int CURRENT_HARDCODED_NUMBER_OF_USER_ICONS = 2;

    private OnRefreshLayoutListener mRefreshLayoutListener;
    private UserView mUser1View;
    private UserView mUser2View;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int[] points = data.getIntArray(DataUpdater.UPDATED_POINTS_DATA);
            if (points != null && mRefreshLayoutListener != null) {
                mUser1View.setBadgeCount(points[0]);
                mUser2View.setBadgeCount(points[1]);
                mRefreshLayoutListener.stopRefreshing();
            }
        }
    };

    private View.OnClickListener mOnTitleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), UsersListActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnRefreshLayoutListener) {
            mRefreshLayoutListener = (OnRefreshLayoutListener) getParentFragment();
        } else {
            throw new IllegalStateException(getParentFragment() + " must implement OnRefreshLayoutListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mUser1View = view.findViewById(R.id.user_1);
        mUser2View = view.findViewById(R.id.user_2);
        view.findViewById(R.id.title).setOnClickListener(mOnTitleListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRefreshLayoutListener = null;
    }

    public void updateBadges() {
        DataUpdater.newInstance(mHandler, CURRENT_HARDCODED_NUMBER_OF_USER_ICONS).start();
    }

    public static AcademicPerformanceFragment newInstance() {
        return new AcademicPerformanceFragment();
    }
}
