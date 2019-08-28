package fr.insapp.insapp.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.ProfileActivity
import fr.insapp.insapp.models.User
import fr.insapp.insapp.utility.Utils
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.user_thumb_macaroon.view.*

/**
 * Created by thomas on 24/02/2017.
 * Kotlin rewrite on 28/08/2019.
 */

class UserRecyclerViewAdapter(
        val users: MutableList<User>,
        private val requestManager: RequestManager,
        private val matchParent: Boolean
) : RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder>() {

    fun addItem(user: User) {
        this.users.add(user)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(parent.inflate(R.layout.user_thumb), requestManager, matchParent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bindUser(user)
    }

    override fun getItemCount() = users.size

    class UserViewHolder(
            private val view: View,
            private val requestManager: RequestManager,
            matchParent: Boolean
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var user: User? = null

        init {
            view.setOnClickListener(this)

            if (matchParent)
                view.user_thumb_macaroon_layout.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        }

        fun bindUser(user: User) {
            this.user = user

            val context = itemView.context

            val id = context.resources.getIdentifier(Utils.drawableProfileName(user.promotion, user.gender), "drawable", context.packageName)
            requestManager
                .load(id)
                .apply(RequestOptions.circleCropTransform())
                .transition(withCrossFade())
                .into(view.user_avatar)

            view.user_name.text = user.name
            view.user_username.text = user.username
        }

        override fun onClick(v: View) {
            val context = itemView.context
            context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("user", user))
        }
    }
}
