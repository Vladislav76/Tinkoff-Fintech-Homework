package com.vladislavmyasnikov.courseproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.data.db.entity.LectureEntity;
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnItemClickCallback;
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder> {

    private List<LectureEntity> mLectures;
    private OnItemClickCallback mCallback;

    public LectureAdapter(OnItemClickCallback callback) {
        mCallback = callback;
    }

    public void updateList(List<LectureEntity> lectures) {
        if (mLectures == null) {
            mLectures = lectures;
            notifyItemRangeInserted(0, lectures.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallback(mLectures, lectures));
            mLectures = lectures;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_lecture, parent, false);
        return new LectureViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        holder.bind(mLectures.get(position));
    }

    @Override
    public int getItemCount() {
        return mLectures == null ? 0 : mLectures.size();
    }

    static class LectureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameView;
        private LectureEntity mLecture;
        private OnItemClickCallback mCallback;

        LectureViewHolder(View view, OnItemClickCallback callback) {
            super(view);
            mCallback = callback;
            mNameView = view.findViewById(R.id.name_field);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onClick(mLecture.getId(), mLecture.getTitle());
        }

        void bind(LectureEntity lecture) {
            mLecture = lecture;
            mNameView.setText(lecture.getTitle());
        }
    }
}