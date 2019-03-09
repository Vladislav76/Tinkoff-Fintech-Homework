package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class UserView extends ConstraintLayout {

    private int mBadgeCount;
    private TextView mUserNameView;
    private TextView mBadgeView;
    private ImageView mUserIconView;

    public UserView(Context context) {
        super(context);
        init(context, null);
    }

    public UserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.user_item, null);
        mUserNameView = view.findViewById(R.id.user_name_field);
        mBadgeView = view.findViewById(R.id.user_points_field);
        mUserIconView = view.findViewById(R.id.user_icon);
        addView(view);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UserView);
            setText(array.getString(R.styleable.UserView_text));
            setBadgeCount(array.getInteger(R.styleable.UserView_badgeCount, 0));
            setIconBackgroundColor(array.getColor(R.styleable.UserView_iconBackgroundColor, 0));
            array.recycle();
        }
    }

    public void setBadgeCount(int value) {
        if (value == 0) {
            mBadgeView.setVisibility(View.GONE);
        } else if (mBadgeCount == 0) {
            mBadgeView.setVisibility(View.VISIBLE);
        }

        mBadgeCount = value;
        mBadgeView.setText(Integer.toString(value));
    }

    public void setIconBackgroundColor(int colorId) {
        mUserIconView.setColorFilter(colorId);
    }

    public void setText(CharSequence name) {
        mUserNameView.setText(name);
    }

    public void setText(int resId) {
        mUserNameView.setText(resId);
    }
}
