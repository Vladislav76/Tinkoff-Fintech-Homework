package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnRefreshLayoutListener;

public class CoursesFragment extends Fragment implements OnRefreshLayoutListener {

    private OnFragmentListener mFragmentListener;
    private AcademicPerformanceFragment mAcademicPerformanceFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String CONTENT_FRAME_1_TAG = "content_frame_1";
    private static final String CONTENT_FRAME_2_TAG = "content_frame_2";
    private static final String CONTENT_FRAME_3_TAG = "content_frame_3";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.courses_toolbar_title);

        addChildFragment(CONTENT_FRAME_1_TAG);
        addChildFragment(CONTENT_FRAME_2_TAG);
        addChildFragment(CONTENT_FRAME_3_TAG);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAcademicPerformanceFragment.updateBadges();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    @Override
    public void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void addChildFragment(String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(CONTENT_FRAME_1_TAG);
        int containerId;

        if (fragment == null) {
            switch (tag) {
                case CONTENT_FRAME_1_TAG:
                    mAcademicPerformanceFragment = AcademicPerformanceFragment.newInstance();
                    fragment = mAcademicPerformanceFragment;
                    containerId = R.id.content_frame_1;
                    break;
                case CONTENT_FRAME_2_TAG:
                    fragment = RatingFragment.newInstance();
                    containerId = R.id.content_frame_2;
                    break;
                case CONTENT_FRAME_3_TAG:
                    fragment = PassedCoursesFragment.newInstance();
                    containerId = R.id.content_frame_3;
                    break;
                default:
                    return;
            }
            fragmentManager.beginTransaction().replace(containerId, fragment).commit();
        }
    }

    public static CoursesFragment newInstance() {
        return new CoursesFragment();
    }
}
