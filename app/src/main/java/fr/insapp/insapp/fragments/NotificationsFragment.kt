package fr.insapp.insapp.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bumptech.glide.Glide

import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.activities.PostActivity
import fr.insapp.insapp.adapters.NotificationRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.models.Notifications
import fr.insapp.insapp.models.Post
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.fragment_notifications.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 27/10/2016.
 */

class NotificationsFragment : Fragment() {

    private var adapter: NotificationRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // adapter

        this.adapter = NotificationRecyclerViewAdapter(context, Glide.with(this))
        adapter!!.setOnItemClickListener { notification ->
            when (notification.type) {
                "tag", "post" -> {
                    val call1 = ServiceGenerator.create().getPostFromId(notification.content)
                    call1.enqueue(object : Callback<Post> {
                        override fun onResponse(call: Call<Post>, response: Response<Post>) {
                            if (response.isSuccessful) {
                                startActivity(Intent(context, PostActivity::class.java).putExtra("post", response.body()))
                            } else {
                                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Post>, t: Throwable) {
                            Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                "eventTag", "event" -> {
                    val call2 = ServiceGenerator.create().getEventFromId(notification.content)
                    call2.enqueue(object : Callback<Event> {
                        override fun onResponse(call: Call<Event>, response: Response<Event>) {
                            if (response.isSuccessful) {
                                startActivity(Intent(context, EventActivity::class.java).putExtra("event", response.body()))
                            } else {
                                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Event>, t: Throwable) {
                            Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                else -> {
                }
            }

            // mark notification as seen

            val user = Utils.getUser()

            val call = ServiceGenerator.create().markNotificationAsSeen(user.id, notification.id)
            call.enqueue(object : Callback<Notifications> {
                override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                    if (response.isSuccessful) {

                    } else {
                        Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Notifications>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                }
            })
        }

        generateNotifications()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_notifications.setHasFixedSize(true)
        recyclerview_notifications.isNestedScrollingEnabled = false

        recyclerview_notifications.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_notifications.adapter = adapter
    }

    private fun generateNotifications() {
        val user = Utils.getUser()

        val call = ServiceGenerator.create().getNotificationsForUser(user.id)
        call.enqueue(object : Callback<Notifications> {
            override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                if (response.isSuccessful) {
                    val notifications = response.body()

                    if (notifications!!.notifications != null) {
                        for (notification in notifications.notifications) {
                            adapter!!.addItem(notification)
                        }
                    }
                } else {
                    Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Notifications>, t: Throwable) {
                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
            }
        })
    }
}