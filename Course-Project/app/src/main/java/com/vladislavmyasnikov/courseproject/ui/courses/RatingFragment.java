package com.vladislavmyasnikov.courseproject.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment;

import androidx.annotation.NonNull;

public class RatingFragment extends GeneralFragment {

    private View.OnClickListener mOnTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LectureListFragment fragment = LectureListFragment.newInstance();
            mFragmentListener.addFragmentOnTop(fragment);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        view.findViewById(R.id.title).setOnClickListener(mOnTitleClickListener);
    }

    public static RatingFragment newInstance() {
        return new RatingFragment();
    }
}
