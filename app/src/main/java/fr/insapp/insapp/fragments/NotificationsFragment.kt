package fr.insapp.insapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.NotificationRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Notifications
import fr.insapp.insapp.utility.DividerItemDecoration
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.no_network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 27/10/2016.
 */

class NotificationsFragment : Fragment() {

    private lateinit var adapter: NotificationRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.adapter = NotificationRecyclerViewAdapter(mutableListOf(), Glide.with(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        generateNotifications()
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_notifications.setHasFixedSize(true)
        recyclerview_notifications.isNestedScrollingEnabled = false

        recyclerview_notifications.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_notifications.adapter = adapter
        recyclerview_notifications.addItemDecoration(DividerItemDecoration(resources, R.drawable.full_divider))
    }

    private fun generateNotifications() {
        no_network?.visibility = View.GONE
        val user = Utils.user

        if (user != null) {
            val call = ServiceGenerator.client.getNotificationsForUser(user.id)
            call.enqueue(object : Callback<Notifications> {
                override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                    val results = response.body()
                    if (response.isSuccessful && results?.notifications != null) {
                        for (notification in results.notifications) {
                            adapter.addItem(notification)
                        }
                    } else if (!response.isSuccessful && recyclerview_notifications != null) {
                        Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Notifications>, t: Throwable) {
                    if (adapter.itemCount == 0) {
                        no_network?.visibility = View.VISIBLE
                    } else if (recyclerview_notifications != null) {
                        Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            Log.d(TAG, "Couldn't fetch notifications: user is null")
        }
    }

    companion object {

        const val TAG = "NotificationsFragment"
    }
}