package com.vladislavmyasnikov.courseproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;

import java.util.Random;

public class AcademicPerformanceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_performance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mUser1View = view.findViewById(R.id.user_1);
        mUser2View = view.findViewById(R.id.user_2);
        mUser3View = view.findViewById(R.id.user_3);
        updateUserItem(mUser1View, "Андрей", R.color.green);
        updateUserItem(mUser2View, "Вы", R.color.colorAccent);
        updateUserItem(mUser3View, "Павел", R.color.colorPrimary);
    }

    private void updateUserItem(View view, String name, int color) {
        TextView textView = view.findViewById(R.id.user_name_field);
        textView.setText(name);

        ImageView imageView = view.findViewById(R.id.user_icon);
        imageView.setColorFilter(getContext().getResources().getColor(color));
    }

    private void updateBadgeValue(@NonNull View view, int newValue) {
        BadgeView badgeView = view.findViewById(R.id.user_points_field);
        if (newValue == 0) {
            badgeView.setVisibility(View.GONE);
        }
        else if (badgeView.getValue() == 0) {
            badgeView.setVisibility(View.VISIBLE);
        }
        badgeView.setValue(newValue);
    }

    public void updateBadges() {
        Random random = new Random();
        updateBadgeValue(mUser1View.findViewById(R.id.user_points_field), random.nextInt(10));
        updateBadgeValue(mUser2View.findViewById(R.id.user_points_field), random.nextInt(10));
        updateBadgeValue(mUser3View.findViewById(R.id.user_points_field), random.nextInt(10));
    }

    private View mUser1View;
    private View mUser2View;
    private View mUser3View;

    public static AcademicPerformanceFragment newInstance() {
        return new AcademicPerformanceFragment();
    }
}
