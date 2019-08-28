package fr.insapp.insapp.fragments

import android.os.Bundle
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
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.fragment_notifications.*
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
    }

    private fun generateNotifications() {
        no_network?.visibility = View.GONE
        val user = Utils.user

        val call = ServiceGenerator.create().getNotificationsForUser(user?.id)
        call.enqueue(object : Callback<Notifications> {
            override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                if (response.isSuccessful) {
                    val notifications = response.body()

                    if (notifications!!.notifications != null) {
                        for (notification in notifications.notifications) {
                            adapter.addItem(notification)
                        }
                    }
                } else {
                    if (adapter.itemCount == 0) {
                        no_network?.visibility = View.VISIBLE
                    } else if (recyclerview_notifications != null){
                        Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Notifications>, t: Throwable) {
                if (adapter.itemCount == 0) {
                    no_network?.visibility = View.VISIBLE
                } else if (recyclerview_notifications != null){
                    Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}