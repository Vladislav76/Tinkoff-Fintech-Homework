package com.vladislavmyasnikov.courseproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;

import java.util.Random;

public class AcademicPerformanceFragment extends Fragment {

    private UserView mUser1View;
    private UserView mUser2View;
    private UserView mUser3View;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mUser1View = view.findViewById(R.id.user_1);
        mUser2View = view.findViewById(R.id.user_2);
        mUser3View = view.findViewById(R.id.user_3);
    }

    private void updateBadgeValue(@NonNull UserView view, int newValue) {
        view.setBadgeCount(newValue);
    }

    public void updateBadges() {
        Random random = new Random();
        updateBadgeValue(mUser1View, random.nextInt(11));
        updateBadgeValue(mUser2View, random.nextInt(11));
        updateBadgeValue(mUser3View, random.nextInt(11));
    }

    public static AcademicPerformanceFragment newInstance() {
        return new AcademicPerformanceFragment();
    }
}
