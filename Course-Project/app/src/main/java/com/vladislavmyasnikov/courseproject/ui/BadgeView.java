package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class BadgeView extends AppCompatTextView {

    public BadgeView(Context context) {
        super(context);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setValue(attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto","badge", 0));
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
        setText(Integer.toString(value));
    }

    private int mValue;
}
