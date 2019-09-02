package fr.insapp.insapp.adapters

import android.content.Intent
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.like.LikeButton
import com.like.OnLikeListener
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.AssociationActivity
import fr.insapp.insapp.activities.PostActivity
import fr.insapp.insapp.activities.isPostLikedBy
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Association
import fr.insapp.insapp.models.Post
import fr.insapp.insapp.models.PostInteraction
import fr.insapp.insapp.utility.Utils
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.post.view.*
import kotlinx.android.synthetic.main.post.view.post_date
import kotlinx.android.synthetic.main.post.view.post_name
import kotlinx.android.synthetic.main.post_with_avatars.view.post_association_avatar
import kotlinx.android.synthetic.main.reactions.view.*
import kotlinx.android.synthetic.main.row_post.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 19/11/2016.
 * Kotlin rewrite on 28/08/2019.
 */

class PostRecyclerViewAdapter(
        val posts: MutableList<Post>,
        private val requestManager: RequestManager,
        private val layout: Int
) : RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder>() {

    fun addItem(post: Post) {
        this.posts.add(post)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder(parent.inflate(layout), requestManager, layout)

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bindPost(post)
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(
            private val view: View,
            private val requestManager: RequestManager,
            private val layoutId: Int
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var post: Post? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindPost(post: Post) {
            this.post = post

            val context = itemView.context

            view.post_name.text = post.title
            view.post_date.text = Utils.displayedDate(post.date)

            // available layouts are row_post, post or post_with_avatars

            if (layoutId != R.layout.post) {

                // association avatar

                val call = ServiceGenerator.create().getAssociationFromId(post.association)
                call.enqueue(object : Callback<Association> {
                    override fun onResponse(call: Call<Association>, response: Response<Association>) {
                        if (response.isSuccessful) {
                            val association = response.body()

                            if (association != null) {
                                requestManager
                                    .load(ServiceGenerator.CDN_URL + association.profilePicture)
                                    .apply(RequestOptions.circleCropTransform())
                                    .transition(withCrossFade())
                                    .into(view.post_association_avatar)

                                // listener

                                view.post_association_avatar.setOnClickListener { context.startActivity(Intent(context, AssociationActivity::class.java).putExtra("association", association)) }
                            }
                        } else {
                            Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Association>, t: Throwable) {
                        Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                })
            }

            if (layoutId == R.layout.row_post) {
                requestManager
                    .load(ServiceGenerator.CDN_URL + post.image)
                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                    .transition(withCrossFade())
                    .into(view.post_thumbnail)
            } else {
                view.post_placeholder.setImageSize(post.imageSize)

                requestManager
                    .load(ServiceGenerator.CDN_URL + post.image)
                    .transition(withCrossFade())
                    .into(view.post_image)

                view.post_text.text = post.description
                view.like_counter.text = String.format(Locale.FRANCE, "%d", post.likes.size)
                view.comment_counter.text = String.format(Locale.FRANCE, "%d", post.comments.size)

                // description links

                Linkify.addLinks(view.post_text, Linkify.ALL)
                Utils.convertToLinkSpan(context, view.post_text)

                // like button

                val user = Utils.user
                if (user != null) {
                    val userId = user.id

                    view.like_button.isLiked = post.isPostLikedBy(userId)

                    view.like_button.setOnLikeListener(object : OnLikeListener {
                        override fun liked(likeButton: LikeButton) {
                            val call = ServiceGenerator.create().likePost(post.id, userId)
                            call.enqueue(object : Callback<PostInteraction> {
                                override fun onResponse(call: Call<PostInteraction>, response: Response<PostInteraction>) {
                                    if (response.isSuccessful) {
                                        if (response.body() != null) {
                                            view.like_counter.text = String.format(Locale.FRANCE, "%d", post.likes.size + 1)
                                        }
                                    } else {
                                        Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<PostInteraction>, t: Throwable) {
                                    Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                                }
                            })
                        }

                        override fun unLiked(likeButton: LikeButton) {
                            val call = ServiceGenerator.create().dislikePost(post.id, userId)
                            call.enqueue(object : Callback<PostInteraction> {
                                override fun onResponse(call: Call<PostInteraction>, response: Response<PostInteraction>) {
                                    if (response.isSuccessful) {
                                        if (response.body() != null) {
                                            view.like_counter.text = String.format(Locale.FRANCE, "%d", post.likes.size - 1)
                                        }
                                    } else {
                                        Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<PostInteraction>, t: Throwable) {
                                    Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    })
                }

                // comment button

                view.comment_button.setOnClickListener { context.startActivity(Intent(context, PostActivity::class.java).putExtra("post", post)) }
            }

            // hide image if necessary

            if (post.image.isEmpty()) {
                view.post_placeholder.visibility = View.GONE
                view.post_image.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            val context = itemView.context
            context.startActivity(Intent(context, PostActivity::class.java).putExtra("post", post))
        }
    }
}
