package com.vladislavmyasnikov.courseproject.presentation.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.domain.entities.Course
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import kotlinx.android.synthetic.main.fragment_rating.*

class RatingFragment : GeneralFragment() {

    private val mOnTitleClickListener = View.OnClickListener {
        val fragment = LectureListFragment.newInstance()
        fragmentController?.addFragmentOnTop(fragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setOnClickListener(mOnTitleClickListener)
    }

    fun updateContent(content: Course) {
        rating_content.visibility = View.VISIBLE
        placeholder.visibility = View.GONE
        points_field.text = String.format("%.2f баллов", content.points)
        general_rating_field.text = String.format("%d/%d", content.ratingPosition, content.studentCount)
        passed_tests_field.text = String.format("%d/%d", content.acceptedTestCount, content.testCount)
        done_homework_field.text = String.format("%d/%d", content.acceptedHomeworkCount, content.homeworkCount)
        lessons_number_field.text = String.format("%d занятий", content.lectureCount)
        past_lessons_number_field.text = String.format("%d занятий", content.pastLectureCount)
        remaining_lessons_number_field.text = String.format("%d занятий", content.remainingLectureCount)
        if (content.lectureCount > 0) {
            progress_bar.progress = 100 * content.pastLectureCount / content.lectureCount
        }
    }

    companion object {
        fun newInstance(): RatingFragment {
            return RatingFragment()
        }
    }
}
