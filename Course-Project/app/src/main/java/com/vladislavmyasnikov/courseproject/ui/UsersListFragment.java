package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.models.User;
import com.vladislavmyasnikov.courseproject.ui.adapters.UserAdapter;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UsersListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private OnFragmentListener mFragmentListener;
    private int mItemsArrangement;

    private static final String ITEMS_ARRANGEMENT = "items_arrangement";
    private static final int LINEAR_ARRANGEMENT = 1;
    private static final int GRID_ARRANGEMENT = 2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mItemsArrangement = LINEAR_ARRANGEMENT;
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = new RecyclerView(inflater.getContext());
        setHasOptionsMenu(true);
        return mRecyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.academic_performance_toolbar_title);
        mAdapter = new UserAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            mItemsArrangement = savedInstanceState.getInt(ITEMS_ARRANGEMENT);
        }
        rearrangeLayout();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.temp_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.items_rearrangement_action:
                mItemsArrangement = mItemsArrangement == LINEAR_ARRANGEMENT ? GRID_ARRANGEMENT : LINEAR_ARRANGEMENT;
                rearrangeLayout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(ITEMS_ARRANGEMENT, mItemsArrangement);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    private void rearrangeLayout() {
        if (mItemsArrangement == LINEAR_ARRANGEMENT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter.setViewType(UserAdapter.LINEAR_USER_VIEW);
        } else if (mItemsArrangement == GRID_ARRANGEMENT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            mAdapter.setViewType(UserAdapter.GRID_USER_VIEW);
        }
    }

    public void updateList(List<User> users) {
        mAdapter.setList(users);
        rearrangeLayout();
    }

    public static UsersListFragment newInstance() {
        return new UsersListFragment();
    }
}
