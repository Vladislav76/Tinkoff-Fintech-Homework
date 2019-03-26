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
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemAnimator;
import com.vladislavmyasnikov.courseproject.ui.components.CustomItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UsersListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private List<User> mUsers;
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
        } else {
            throw new IllegalStateException(context + " must implement OnFragmentListener");
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
        mAdapter = new UserAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new CustomItemDecoration(10));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
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
            case R.id.list_rearrangement_action:
                mItemsArrangement = mItemsArrangement == LINEAR_ARRANGEMENT ? GRID_ARRANGEMENT : LINEAR_ARRANGEMENT;
                rearrangeLayout();
                return true;
            case R.id.list_item_adding_action:
                mUsers.add(generateNewUser());
                mAdapter.notifyItemInserted(mUsers.size() - 1);
                return true;
            case R.id.list_item_removing_action:
                int userId = mUsers.size() - 1;
                mUsers.remove(userId);
                mAdapter.notifyItemRemoved(userId);
                return true;
            case R.id.list_mixing_action:
                List<User> mixedList = getMixedList();
                mUsers = mixedList;
                mRecyclerView.getLayoutManager().scrollToPosition(0);
                mAdapter.setList(mixedList);
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

    public void updateList(List<User> users) {
        mUsers = users;
        mAdapter.setList(users);
        rearrangeLayout();
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

    private User generateNewUser() {
        Random random = new Random();
        int surnameId = random.nextInt(999999);
        return new User("User", Integer.toString(surnameId), random.nextInt(500));
    }

    private List<User> getMixedList() {
        List<User> mixedList = new ArrayList<>(mUsers.size());
        Random random = new Random();
        int currentSize = 0;
        for (User user : mUsers) {
            User clonedUser = new User(user);
            currentSize++;
            mixedList.add(random.nextInt(currentSize), clonedUser);
        }
        return mixedList;
    }

    public static UsersListFragment newInstance() {
        return new UsersListFragment();
    }
}
