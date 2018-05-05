package fr.insapp.insapp.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Club
import fr.insapp.insapp.models.Event
import kotlinx.android.synthetic.main.fragment_events_club.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 09/12/2016.
 */

class EventsClubFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var layout: Int = 0
    private var swipeColor: Int = 0

    private var club: Club? = null

    private var adapterFuture: EventRecyclerViewAdapter? = null
    private var adapterPast: EventRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments

        val bundle = arguments
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.row_event)
            this.club = bundle.getParcelable("club")
            this.swipeColor = bundle.getInt("swipe_color")
        }

        // adapters

        val requestManager = Glide.with(this)

        this.adapterFuture = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterFuture!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        this.adapterPast = EventRecyclerViewAdapter(context, requestManager, true, layout)
        adapterPast!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events_club, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_events_future.setHasFixedSize(true)
        recyclerview_events_future.isNestedScrollingEnabled = false

        recyclerview_events_past.setHasFixedSize(true)
        recyclerview_events_past.isNestedScrollingEnabled = false

        recyclerview_events_future.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_future.adapter = adapterFuture

        recyclerview_events_past.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_past.adapter = adapterPast

        // swipe refresh layout

        refresh_events_club.setOnRefreshListener(this)
        refresh_events_club.setColorSchemeColors(swipeColor)

        generateEvents()
    }

    private fun clearEvents() {
        adapterFuture!!.events.clear()
        adapterPast!!.events.clear()

        events_future_layout.visibility = View.GONE
        events_past_layout.visibility = View.GONE
    }

    private fun generateEvents() {
        clearEvents()

        for (j in 0 until club!!.events.size) {
            val call = ServiceGenerator.create().getEventFromId(club!!.events[j])
            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.isSuccessful) {
                        addEventToAdapter(response.body()!!)
                    } else {
                        Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show()
                    }

                    refresh_events_club.isRefreshing = false
                }

                override fun onFailure(call: Call<Event>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show()

                    refresh_events_club.isRefreshing = false
                }
            })
        }
    }

    private fun addEventToAdapter(event: Event) {
        val atm = Calendar.getInstance().time

        if (event.dateEnd.time > atm.time) {
            adapterFuture!!.addItem(event)
            events_future_layout.visibility = View.VISIBLE
        } else {
            adapterPast!!.addItem(event)
            events_past_layout.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == EVENT_REQUEST) {
            when (resultCode) {
                RESULT_OK -> {
                    val event = intent!!.getParcelableExtra<Event>("event")

                    for (i in 0 until adapterPast!!.itemCount) {
                        if (adapterPast!!.events[i].id == event.id) {
                            adapterPast!!.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterFuture!!.itemCount) {
                        if (adapterFuture!!.events[i].id == event.id) {
                            adapterFuture!!.updateEvent(i, event)
                        }
                    }
                }

                RESULT_CANCELED -> {
                }
                else -> {
                }
            }
        }
    }

    override fun onRefresh() {
        generateEvents()
    }

    companion object {

        private val EVENT_REQUEST = 2
    }
}
