package com.vladislavmyasnikov.courseproject.ui.main.interfaces;

import androidx.fragment.app.Fragment;

public interface OnFragmentListener {

    void addFragmentOnTop(Fragment fragment);
    void setToolbarTitle(int titleId);
    void setToolbarTitle(CharSequence title);
}
