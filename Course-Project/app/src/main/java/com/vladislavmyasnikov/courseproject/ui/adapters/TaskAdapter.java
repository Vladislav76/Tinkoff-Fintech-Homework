package com.vladislavmyasnikov.courseproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.data.db.entity.TaskEntity;
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskEntity> mTasks;

    public void updateList(List<TaskEntity> tasks) {
        if (mTasks == null) {
            mTasks = tasks;
            notifyItemRangeInserted(0, tasks.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilCallback(mTasks, tasks));
            mTasks = tasks;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(mTasks.get(position));
    }

    @Override
    public int getItemCount() {
        return mTasks == null ? 0 : mTasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameView;
        private TextView mDeadlineView;
        private TextView mStatusView;
        private TextView mMarkView;

        TaskViewHolder(View view) {
            super(view);
            mNameView = view.findViewById(R.id.name_field);
            mDeadlineView = view.findViewById(R.id.deadline_field);
            mStatusView = view.findViewById(R.id.status_field);
            mMarkView = view.findViewById(R.id.mark_field);
        }

        void bind(TaskEntity task) {
            mNameView.setText(task.getTitle());
            mStatusView.setText(task.getStatus());
            mMarkView.setText(String.format(Locale.getDefault(), "%.2f/%.2f", task.getMark(), task.getMaxScore()));

            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US);
            Date date = task.getDeadline();
            if (date != null) {
                mDeadlineView.setText(formatter.format(date));
            }
        }
    }
}