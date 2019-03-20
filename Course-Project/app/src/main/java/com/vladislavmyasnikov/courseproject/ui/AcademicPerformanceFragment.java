package com.vladislavmyasnikov.courseproject.ui;

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
import com.vladislavmyasnikov.courseproject.ui.components.UserView;

import java.util.Random;

public class AcademicPerformanceFragment extends Fragment {

    private static final String UPDATED_POINTS_DATA = "updated_points_data";
    private static final int CURRENT_HARDCODED_NUMBER_OF_USER_ICONS = 2;

    private UserView mUser1View;
    private UserView mUser2View;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int[] points = data.getIntArray(UPDATED_POINTS_DATA);
            if (points != null) {
                mUser1View.setBadgeCount(points[0]);
                mUser2View.setBadgeCount(points[1]);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mUser1View = view.findViewById(R.id.user_1);
        mUser2View = view.findViewById(R.id.user_2);
        view.findViewById(R.id.title).setOnClickListener(mOnTitleListener);
    }

    public void updateBadges() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Bundle data = new Bundle();
                Random random = new Random();
                int[] points = new int[CURRENT_HARDCODED_NUMBER_OF_USER_ICONS];
                for (int i = 0; i < points.length; i++) {
                    points[i] = random.nextInt(11);
                }
                data.putIntArray(UPDATED_POINTS_DATA, points);
                message.setData(data);
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public static AcademicPerformanceFragment newInstance() {
        return new AcademicPerformanceFragment();
    }
}
