package fr.insapp.insapp.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Association
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

    private var club: Association? = null

    private lateinit var adapterFuture: EventRecyclerViewAdapter
    private lateinit var adapterPast: EventRecyclerViewAdapter

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

        this.adapterFuture = EventRecyclerViewAdapter(mutableListOf(), requestManager, false, layout)
        this.adapterPast = EventRecyclerViewAdapter(mutableListOf(), requestManager, true, layout)
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
        adapterFuture.events.clear()
        adapterPast.events.clear()

        events_future_layout.visibility = View.GONE
        events_past_layout.visibility = View.GONE
    }

    private fun generateEvents() {
        clearEvents()

        val call = ServiceGenerator.create().getEventsForAssociation(club?.id)
        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    addEventsToAdapter(response.body())
                } else {
                    Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show()
                }

                refresh_events_club?.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show()

                refresh_events_club?.isRefreshing = false
            }
        })
    }

    private fun addEventsToAdapter(events: List<Event>?) {
        if (events != null) {
            val atm = Calendar.getInstance().time

            for (event in events) {
                if (event.dateEnd.time > atm.time) {
                    adapterFuture.addItem(event)
                    events_future_layout?.visibility = View.VISIBLE
                } else {
                    adapterPast.addItem(event)
                    events_past_layout?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == EVENT_REQUEST) {
            when (resultCode) {
                RESULT_OK -> {
                    val event = intent!!.getParcelableExtra<Event>("event")

                    for (i in 0 until adapterPast.itemCount) {
                        if (adapterPast.events[i].id == event.id) {
                            adapterPast.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterFuture.itemCount) {
                        if (adapterFuture.events[i].id == event.id) {
                            adapterFuture.updateEvent(i, event)
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

        private const val EVENT_REQUEST = 2
    }
}
