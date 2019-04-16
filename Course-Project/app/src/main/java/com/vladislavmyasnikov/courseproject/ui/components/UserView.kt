package com.vladislavmyasnikov.courseproject.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.vladislavmyasnikov.courseproject.R

class UserView : ConstraintLayout {

    private var mBadgeCount: Int = 0
    private var mUserNameView: TextView? = null
    private var mBadgeView: TextView? = null
    private var mUserIconView: ImageView? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.user_item, this)
        mUserNameView = view.findViewById(R.id.user_name_field)
        mBadgeView = view.findViewById(R.id.user_points_field)
        mUserIconView = view.findViewById(R.id.user_icon)

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UserView)
            setText(array.getString(R.styleable.UserView_text))
            setBadgeCount(array.getInteger(R.styleable.UserView_badgeCount, 0))
            setIconBackgroundColor(array.getColor(R.styleable.UserView_iconBackgroundColor, 0))
            array.recycle()
        }
    }

    fun setBadgeCount(value: Int) {
        if (value == 0) {
            mBadgeView?.visibility = View.GONE
        } else if (mBadgeCount == 0) {
            mBadgeView?.visibility = View.VISIBLE
        }

        mBadgeCount = value
        mBadgeView?.text = value.toString()
    }

    fun setIconBackgroundColor(colorId: Int) {
        mUserIconView?.setColorFilter(colorId)
    }

    fun setText(name: CharSequence?) {
        mUserNameView?.text = name
    }

    fun setText(resId: Int) {
        mUserNameView?.setText(resId)
    }
}
