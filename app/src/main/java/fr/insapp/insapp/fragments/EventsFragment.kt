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
import com.bumptech.glide.Glide
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Event
import kotlinx.android.synthetic.main.fragment_events.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 27/10/2016.
 */

class EventsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var layout: Int = 0
    private var filter_club_id: String? = null

    private var adapterNow: EventRecyclerViewAdapter? = null
    private var adapterToday: EventRecyclerViewAdapter? = null
    private var adapterWeek: EventRecyclerViewAdapter? = null
    private var adapterNextWeek: EventRecyclerViewAdapter? = null
    private var adapterLater: EventRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments

        val bundle = arguments
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.row_event_with_avatars)
            this.filter_club_id = bundle.getString("filter_club_id")
        }

        // adapters

        val requestManager = Glide.with(this)

        this.adapterNow = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterNow!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        this.adapterToday = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterToday!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        this.adapterWeek = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterWeek!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        this.adapterNextWeek = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterNextWeek!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        this.adapterLater = EventRecyclerViewAdapter(context, requestManager, false, layout)
        adapterLater!!.setOnItemClickListener { event -> startActivityForResult(Intent(context, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view

        recyclerview_events_now.setHasFixedSize(true)
        recyclerview_events_now.isNestedScrollingEnabled = false

        recyclerview_events_today.setHasFixedSize(true)
        recyclerview_events_today.isNestedScrollingEnabled = false

        recyclerview_events_week.setHasFixedSize(true)
        recyclerview_events_week.isNestedScrollingEnabled = false

        recyclerview_events_next_week.setHasFixedSize(true)
        recyclerview_events_next_week.isNestedScrollingEnabled = false

        recyclerview_events_later.setHasFixedSize(true)
        recyclerview_events_later.isNestedScrollingEnabled = false

        recyclerview_events_now.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_now.adapter = adapterNow

        recyclerview_events_today.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_today.adapter = adapterToday

        recyclerview_events_week.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_week.adapter = adapterWeek

        recyclerview_events_next_week.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_next_week.adapter = adapterNextWeek

        recyclerview_events_later.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_later.adapter = adapterLater

        // swipe refresh layout

        refresh_events.setOnRefreshListener(this)
        refresh_events.setColorSchemeResources(R.color.colorPrimary)

        generateEvents()
    }

    private fun clearEvents() {
        adapterNow!!.events.clear()
        adapterToday!!.events.clear()
        adapterWeek!!.events.clear()
        adapterNextWeek!!.events.clear()
        adapterLater!!.events.clear()

        events_now_layout?.visibility = View.GONE
        events_today_layout?.visibility = View.GONE
        events_week_layout?.visibility = View.GONE
        events_next_week_layout?.visibility = View.GONE
        events_later_layout?.visibility = View.GONE
    }

    private fun generateEvents() {
        clearEvents()

        progress_bar?.visibility = View.VISIBLE
        refresh_events?.isRefreshing = true

        no_network?.visibility = View.INVISIBLE
        no_event?.visibility = View.INVISIBLE

        val call = ServiceGenerator.create().futureEvents
        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body()
                    val atm = Calendar.getInstance().time

                    if (events != null) {
                        if (events.isNotEmpty()) {
                            no_event?.visibility = View.GONE
                            for (event in events) {
                                if (event.dateEnd.time > atm.time) {
                                    if (filter_club_id != null) {
                                        if (filter_club_id == event.association) {
                                            addEventToAdapter(event)
                                        }
                                    } else {
                                        addEventToAdapter(event)
                                    }
                                }
                            }
                        } else {
                            no_event?.visibility = View.VISIBLE
                        }
                    }
                } else {
                    //Toast.makeText(App.getAppContext(), "EventsFragment", Toast.LENGTH_LONG).show()
                    no_network?.visibility = View.VISIBLE
                }

                stopLoadingIndicators()
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                //Toast.makeText(App.getAppContext(), "EventsFragment - Veuillez vérifier votre connection internet", Toast.LENGTH_LONG).show()
                stopLoadingIndicators()
                no_network?.visibility = View.VISIBLE
                no_event?.visibility = View.INVISIBLE
            }
        })
    }

    private fun addEventToAdapter(event: Event) {
        val now = Calendar.getInstance()

        if (event.dateStart.time <= now.time.time && event.dateEnd.time > now.time.time) {
            adapterNow!!.addItem(event)
            events_now_layout?.visibility = View.VISIBLE
            return
        }

        val tomorrow = Calendar.getInstance()

        // tomorrow midnight

        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)


        if (event.dateStart.time <= tomorrow.time.time) {
            adapterToday!!.addItem(event)
            events_today_layout?.visibility = View.VISIBLE
            return
        }

        // saturday at midday

        val week = Calendar.getInstance()

        while (week.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            week.add(Calendar.DATE, 1)
        }

        week.set(Calendar.HOUR_OF_DAY, 12)
        week.set(Calendar.MINUTE, 0)
        week.set(Calendar.SECOND, 0)
        week.set(Calendar.MILLISECOND, 0)

        if (event.dateStart.time <= week.time.time) {
            adapterWeek!!.addItem(event)
            events_week_layout?.visibility = View.VISIBLE
            return
        }

        // saturday (next week) at midday

        val nextWeek = Calendar.getInstance()

        while (nextWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            nextWeek.add(Calendar.DATE, 1)
        }

        nextWeek.set(Calendar.HOUR_OF_DAY, 12)
        nextWeek.set(Calendar.MINUTE, 0)
        nextWeek.set(Calendar.SECOND, 0)
        nextWeek.set(Calendar.MILLISECOND, 0)
        nextWeek.add(Calendar.WEEK_OF_MONTH, 1)

        if (event.dateStart.time <= nextWeek.time.time) {
            adapterNextWeek!!.addItem(event)
            events_next_week_layout?.visibility = View.VISIBLE
            return
        }

        adapterLater!!.addItem(event)
        events_later_layout?.visibility = View.VISIBLE
    }

    private fun stopLoadingIndicators(){
        progress_bar?.visibility = View.INVISIBLE
        refresh_events?.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == EVENT_REQUEST) {
            when (resultCode) {
                RESULT_OK -> {
                    val event = intent!!.getParcelableExtra<Event>("event")

                    for (i in 0 until adapterNow!!.itemCount) {
                        if (adapterNow!!.events[i].id == event.id) {
                            adapterNow!!.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterToday!!.itemCount) {
                        if (adapterToday!!.events[i].id == event.id) {
                            adapterToday!!.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterWeek!!.itemCount) {
                        if (adapterWeek!!.events[i].id == event.id) {
                            adapterWeek!!.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterNextWeek!!.itemCount) {
                        if (adapterNextWeek!!.events[i].id == event.id) {
                            adapterNextWeek!!.updateEvent(i, event)
                        }
                    }

                    for (i in 0 until adapterLater!!.itemCount) {
                        if (adapterLater!!.events[i].id == event.id) {
                            adapterLater!!.updateEvent(i, event)
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