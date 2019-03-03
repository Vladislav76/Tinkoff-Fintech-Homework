package com.vladislavmyasnikov.courseproject.ui.callbacks;

import androidx.fragment.app.Fragment;

public interface OnFragmentListener {

    void setToolbarTitle(int titleId);
    void addFragmentOnTop(Fragment fragment);
}
