package com.vladislavmyasnikov.courseproject.ui.components

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.vladislavmyasnikov.courseproject.R

class InitialsRoundView : FrameLayout {

    private var mInitialsView: TextView? = null
    private var mIconView: ImageView? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.icon_user, this)
        mInitialsView = view.findViewById(R.id.user_initials_field)
        mIconView = view.findViewById(R.id.user_icon_background)
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.InitialsRoundView)
            setText(array.getString(R.styleable.InitialsRoundView_initials))
            setIconColor(array.getColor(R.styleable.InitialsRoundView_roundColor, 0))
            array.recycle()
        }
    }

    fun setText(initials: CharSequence?) {
        mInitialsView!!.text = initials
    }

    fun setIconColor(color: Int) {
        mIconView!!.setColorFilter(color)
    }
}
