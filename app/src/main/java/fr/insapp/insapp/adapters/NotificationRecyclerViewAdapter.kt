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
import fr.insapp.insapp.activities.ClubActivity
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.activities.PostActivity
import fr.insapp.insapp.activities.ProfileActivity
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.*
import fr.insapp.insapp.utility.Utils
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.row_notification.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 11/12/2016.
 * Kotlin rewrite on 28/08/2019.
 */

class NotificationRecyclerViewAdapter(
        private val notifications: MutableList<Notification>,
        private val requestManager: RequestManager
) : RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder>() {

    fun addItem(notification: Notification) {
        this.notifications.add(notification)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotificationViewHolder(parent.inflate(R.layout.row_notification), requestManager)

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bindNotification(notification)
    }

    override fun getItemCount() = notifications.size

    class NotificationViewHolder(
            private val view: View,
            private val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var notification: Notification? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindNotification(notification: Notification) {
            this.notification = notification

            val context = itemView.context

            view.notification_text.text = notification.message
            view.notification_date.text = Utils.displayedDate(notification.date)

            // avatars

            if (notification.type == "tag" || notification.type == "eventTag") {
                val call = ServiceGenerator.create().getUserFromId(notification.sender)
                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()

                            if (user != null) {
                                val id = context.resources.getIdentifier(Utils.drawableProfileName(user.promotion, user.gender), "drawable", context.packageName)
                                requestManager
                                    .load(id)
                                    .apply(RequestOptions.circleCropTransform())
                                    .transition(withCrossFade())
                                    .into(view.avatar_notification)

                                view.avatar_notification.setOnClickListener { context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("user", user)) }
                            }
                        } else {
                            Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            } else if (notification.type == "post" || notification.type == "event") {
                val call = ServiceGenerator.create().getClubFromId(notification.sender)
                call.enqueue(object : Callback<Club> {
                    override fun onResponse(call: Call<Club>, response: Response<Club>) {
                        if (response.isSuccessful) {
                            val club = response.body()

                            if (club != null) {
                                requestManager
                                    .load(ServiceGenerator.CDN_URL + club.profilePicture)
                                    .apply(RequestOptions.circleCropTransform())
                                    .transition(withCrossFade())
                                    .into(view.avatar_notification)

                                view.avatar_notification.setOnClickListener { context.startActivity(Intent(context, ClubActivity::class.java).putExtra("club", club)) }
                            }
                        } else {
                            Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Club>, t: Throwable) {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            }

            // thumbnails

            if (notification.type == "tag" || notification.type == "post") {
                val call = ServiceGenerator.create().getPostFromId(notification.content)
                call.enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (response.isSuccessful) {
                            val post = response.body()

                            if (post != null) {
                                requestManager
                                    .load(ServiceGenerator.CDN_URL + post.image)
                                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                                    .transition(withCrossFade())
                                    .into(view.notification_thumbnails)
                            }
                        } else {
                            Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            } else if (notification.type == "eventTag" || notification.type == "event") {
                val call = ServiceGenerator.create().getEventFromId(notification.content)
                call.enqueue(object : Callback<Event> {
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        if (response.isSuccessful) {
                            val event = response.body()

                            if (event != null) {
                                requestManager
                                    .load(ServiceGenerator.CDN_URL + event.image)
                                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                                    .transition(withCrossFade())
                                    .into(view.notification_thumbnails)
                            }
                        } else {
                            Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        override fun onClick(v: View) {
            val context = itemView.context

            when (notification?.type) {
                "tag", "post" -> {
                    val call1 = ServiceGenerator.create().getPostFromId(notification?.content)
                    call1.enqueue(object : Callback<Post> {
                        override fun onResponse(call: Call<Post>, response: Response<Post>) {
                            if (response.isSuccessful) {
                                context.startActivity(Intent(context, PostActivity::class.java).putExtra("post", response.body()))
                            }
                            /*
                            else if (recyclerview_notifications != null){
                                Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                            }
                            */
                        }

                        override fun onFailure(call: Call<Post>, t: Throwable) {
                            /*
                            if (recyclerview_notifications != null){
                                Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                            }
                            */
                        }
                    })
                }

                "eventTag", "event" -> {
                    val call2 = ServiceGenerator.create().getEventFromId(notification?.content)
                    call2.enqueue(object : Callback<Event> {
                        override fun onResponse(call: Call<Event>, response: Response<Event>) {
                            if (response.isSuccessful) {
                                context.startActivity(Intent(context, EventActivity::class.java).putExtra("event", response.body()))
                            }
                            /*
                            else if (recyclerview_notifications != null){
                                Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                            }
                            */
                        }

                        override fun onFailure(call: Call<Event>, t: Throwable) {
                            /*
                            if (recyclerview_notifications != null){
                                Snackbar.make(recyclerview_notifications, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                            }
                            */
                        }
                    })
                }

                else -> {
                }
            }

            // mark notification as seen

            val user = Utils.user

            val call = ServiceGenerator.create().markNotificationAsSeen(user?.id, notification?.id)
            call.enqueue(object : Callback<Notifications> {
                override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                    if (response.isSuccessful) {

                    } else {
                        // Il y a déjà une snackbar qui s'ouvre
                        //Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Notifications>, t: Throwable) {
                    // Il y a déjà une snackbar qui s'ouvre
                    //Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
