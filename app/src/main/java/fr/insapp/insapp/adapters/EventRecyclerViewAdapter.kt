package fr.insapp.insapp.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.AssociationActivity
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Association
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.row_event.view.*
import kotlinx.android.synthetic.main.row_event_with_avatars.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thomas on 18/11/2016.
 * Kotlin rewrite on 28/08/2019.
 */

class EventRecyclerViewAdapter(
        var events: MutableList<Event>,
        private val requestManager: RequestManager,
        private val past: Boolean,
        private val layout: Int
) : RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder>() {

    fun addItem(event: Event) {
        this.events.add(event)
        Collections.sort(events, EventComparator(past))

        this.notifyDataSetChanged()
    }

    fun updateEvent(position: Int, event: Event) {
        events[position] = event
        notifyItemChanged(position)
    }

    fun removeItem(eventId: String) {
        for (event in events) {
            if (event.id == eventId) {
                events.remove(event)

                this.notifyDataSetChanged()
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(parent.inflate(layout), requestManager, layout)

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bindEvent(event)
    }

    override fun getItemCount() = events.size

    class EventViewHolder(
            private val view: View,
            private val requestManager: RequestManager,
            private val layoutId: Int
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var event: Event? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindEvent(event: Event) {
            this.event = event

            val context = itemView.context

            if (layoutId == R.layout.row_event_with_avatars) {
                val call = ServiceGenerator.client.getAssociationFromId(event.association)
                call.enqueue(object : Callback<Association> {
                    override fun onResponse(call: Call<Association>, response: Response<Association>) {
                        if (response.isSuccessful) {
                            val association = response.body()

                            if (association != null) {
                                requestManager
                                    .load(ServiceGenerator.CDN_URL + association.profilePicture)
                                    .apply(RequestOptions.circleCropTransform())
                                    .transition(withCrossFade())
                                    .into(view.event_association_avatar)

                                view.event_association_avatar.setOnClickListener { context.startActivity(Intent(context, AssociationActivity::class.java).putExtra("association", association)) }
                            }
                        } else {
                            Toast.makeText(context, "EventRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Association>, t: Throwable) {
                        Toast.makeText(context, "EventRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            }

            requestManager
                .load(ServiceGenerator.CDN_URL + event.image)
                .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                .into(view.event_thumbnail)

            view.event_name.text = event.name

            val nbAttendees = if (event.attendees == null) 0 else event.attendees.size
            if (nbAttendees <= 1) {
                view.event_going.text = String.format(context.getString(R.string.x_attendee), nbAttendees)
            } else {
                view.event_going.text = String.format(context.getString(R.string.x_attendees), nbAttendees)
            }

            val diffInDays = ((event.dateEnd.time - event.dateStart.time) / (1000 * 60 * 60 * 24)).toInt()
            if (diffInDays < 1 && event.dateStart.month == event.dateEnd.month) {
                val dateFormatOneDay = SimpleDateFormat("'Le' dd/MM 'à' HH:mm")

                view.event_date.text = dateFormatOneDay.format(event.dateStart)
            } else {
                val dateFormat = SimpleDateFormat("dd/MM")
                val dateStart = dateFormat.format(event.dateStart)
                val dateEnd = dateFormat.format(event.dateEnd)

                view.event_date.text = "Du $dateStart au $dateEnd"
            }
        }

        override fun onClick(v: View) {
            val context = itemView.context
            context.startActivity(Intent(context, EventActivity::class.java).putExtra("event", event))
        }
    }

    class EventComparator(private val past: Boolean) : Comparator<Event> {

        override fun compare(event1: Event, event2: Event): Int {
            if (event1.dateStart.time == event2.dateStart.time)
                return 0
            if (event1.dateStart.time < event2.dateStart.time)
                return if (past) 1 else -1

            return if (past) -1 else 1
        }
    }
}