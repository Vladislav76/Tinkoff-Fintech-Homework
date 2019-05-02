package com.vladislavmyasnikov.courseproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.domain.entities.Event
import com.vladislavmyasnikov.courseproject.domain.entities.EventTypeColor
import com.vladislavmyasnikov.courseproject.utilities.DiffUtilCallback
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    enum class ViewType {
        COMPACT_VIEW, DETAILED_VIEW
    }

    var viewType: ViewType = ViewType.COMPACT_VIEW
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var items: List<Event> = emptyList()

    fun updateList(_items: List<Event>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(items, _items))
        items = _items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewTypeOrdinal: Int): ViewHolder =
            when (ViewType.values()[viewTypeOrdinal]) {
                ViewType.COMPACT_VIEW -> ViewHolder(createView(parent, viewTypeOrdinal))
                ViewType.DETAILED_VIEW -> ExtendedViewHolder(createView(parent, viewTypeOrdinal))
            }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = viewType.ordinal

    private fun createView(parent: ViewGroup, viewTypeOrdinal: Int): View {
        val inflater = LayoutInflater.from(parent.context)
        val layoutItemId =
                when (ViewType.values()[viewTypeOrdinal]) {
                    ViewType.COMPACT_VIEW -> R.layout.item_compact_event
                    ViewType.DETAILED_VIEW -> R.layout.item_detailed_event
                }
        return inflater.inflate(layoutItemId, parent, false)
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.title)
        private val eventTypeName = view.findViewById<TextView>(R.id.event_type_name)
        private val timePeriod = view.findViewById<TextView>(R.id.time_period)
        private val icon = view.findViewById<ImageView>(R.id.icon)

        open fun bind(item: Event) {
            title.text = item.title
            eventTypeName.text = item.eventTypeName ?: "Мероприятия"

            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startDate = item.startDate?.let { format.format(it) } ?: ""
            val endDate = item.endDate?.let { format.format(it) } ?: ""
            timePeriod.text = String.format("%s - %s", startDate, endDate)

            val iconRes = when (item.eventTypeColor) {
                EventTypeColor.PURPLE -> R.drawable.group_3
                EventTypeColor.ORANGE -> R.drawable.group_8
                EventTypeColor.GREEN -> R.drawable.group_9
                else -> R.drawable.group_6
            }
            icon.setImageResource(iconRes)
        }
    }

    class ExtendedViewHolder(view: View) : ViewHolder(view) {
        private val description = view.findViewById<TextView>(R.id.description)
        private val place = view.findViewById<TextView>(R.id.place)

        override fun bind(item: Event) {
            super.bind(item)
            if (item.description == "") {
                description.visibility = View.GONE
            } else {
                description.visibility = View.VISIBLE
                description.text = item.description
            }

            if (item.place == "") {
                place.visibility = View.GONE
            } else {
                place.visibility = View.VISIBLE
                place.text = item.place
            }
        }
    }
}