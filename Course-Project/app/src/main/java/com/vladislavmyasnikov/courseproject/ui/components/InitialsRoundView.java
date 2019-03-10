package com.vladislavmyasnikov.courseproject.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;

public class InitialsRoundView extends FrameLayout {

    private TextView mInitialsView;
    private ImageView mIconView;

    public InitialsRoundView(Context context) {
        super(context);
        init(null);
    }

    public InitialsRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public InitialsRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.user_icon, this);
        mInitialsView = view.findViewById(R.id.user_initials_field);
        mIconView = view.findViewById(R.id.user_icon_background);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.InitialsRoundView);
            setText(array.getString(R.styleable.InitialsRoundView_initials));
            setIconColor(array.getColor(R.styleable.InitialsRoundView_roundColor, 0));
            array.recycle();
        }
    }

    public void setText(CharSequence initials) {
        mInitialsView.setText(initials);
    }

    public void setIconColor(int color) {
        mIconView.setColorFilter(color);
    }
}
