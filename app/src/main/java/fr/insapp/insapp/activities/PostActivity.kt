package fr.insapp.insapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.like.LikeButton
import com.like.OnLikeListener
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.*
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 12/11/2016.
 */

class PostActivity : AppCompatActivity() {

    private lateinit var adapter: CommentRecyclerViewAdapter

    private lateinit var post: Post
    private var association: Association? = null

    private lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestManager = Glide.with(this)

        val user = Utils.user

        // post

        if (intent.getParcelableExtra<Post>("post") != null) {
            // coming from navigation

            this.post = intent.getParcelableExtra("post")
            generateActivity()

            // mark notification as seen

            if (intent.getParcelableExtra<Notification>("notification") != null) {
                val notification = intent.getParcelableExtra<Notification>("notification")

                if (user != null) {
                    val call = ServiceGenerator.create().markNotificationAsSeen(user.id, notification.id)
                    call.enqueue(object : Callback<Notifications> {
                        override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                            if (!response.isSuccessful) {
                                Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Notifications>, t: Throwable) {
                            Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.d(TAG, "")
                }
            }
        } else {
            // coming from notification
            setContentView(R.layout.loading)

            val call = ServiceGenerator.create().getPostFromId(intent.getStringExtra("ID"))
            call.enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        post = result
                        generateActivity()
                    } else {
                        Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Toast.makeText(this@PostActivity, R.string.check_internet_connection, Toast.LENGTH_LONG).show()

                    // Open the application
                    startActivity(Intent(this@PostActivity, MainActivity::class.java))
                    finish()
                }
            })
        }
    }

    private fun generateActivity() {
        setContentView(R.layout.activity_post)

        // toolbar

        setSupportActionBar(post_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = Utils.user

        // Answers

        Answers.getInstance().logContentView(ContentViewEvent()
            .putContentId(post.id)
            .putContentName(post.title)
            .putContentType("Post")
            .putCustomAttribute("Favorites count", post.likes.size)
            .putCustomAttribute("Comments count", post.comments.size))

        // hide image if necessary

        if (post.image.isEmpty()) {
            post_placeholder?.visibility = View.GONE
            post_image?.visibility = View.GONE
        }

        // like button

        user?.let {
            post_like_button?.isLiked = post.isPostLikedBy(user.id)
        }
        post_like_counter?.text = post.likes.size.toString()

        post_like_button?.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                post_like_counter?.text = (Integer.valueOf(post_like_counter?.text as String) + 1).toString()

                if (user != null) {
                    val call = ServiceGenerator.create().likePost(post.id, user.id)
                    call.enqueue(object : Callback<PostInteraction> {
                        override fun onResponse(call: Call<PostInteraction>, response: Response<PostInteraction>) {
                            val results = response.body()
                            if (response.isSuccessful && results != null) {
                                post = results.post
                            } else {
                                Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<PostInteraction>, t: Throwable) {
                            Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.d(TAG, "Couldn't update the like status: user is null")
                }
            }

            override fun unLiked(likeButton: LikeButton) {
                post_like_counter?.text = (Integer.valueOf(post_like_counter?.text as String) - 1).toString()

                if (Integer.valueOf(post_like_counter?.text as String) < 0) {
                    post_like_counter?.text = "0"
                }

                if (user != null) {
                    val call = ServiceGenerator.create().dislikePost(post.id, user.id)
                    call.enqueue(object : Callback<PostInteraction> {
                        override fun onResponse(call: Call<PostInteraction>, response: Response<PostInteraction>) {
                            val result = response.body()
                            if (response.isSuccessful && result != null) {
                                post = result.post
                            } else {
                                Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<PostInteraction>, t: Throwable) {
                            Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.d(TAG, "Couldn't update the like status: user is null")
                }
            }
        })

        val call = ServiceGenerator.create().getAssociationFromId(post.association)
        call.enqueue(object : Callback<Association> {
            override fun onResponse(call: Call<Association>, response: Response<Association>) {
                if (response.isSuccessful) {
                    association = response.body()

                    requestManager
                        .load(ServiceGenerator.CDN_URL + association!!.profilePicture)
                        .apply(RequestOptions.circleCropTransform())
                        .transition(withCrossFade())
                        .into(post_association_avatar)

                    // listener

                    post_association_avatar?.setOnClickListener { startActivity(Intent(this@PostActivity, AssociationActivity::class.java).putExtra("association", association)) }
                } else {
                    Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Association>, t: Throwable) {
                Toast.makeText(this@PostActivity, TAG, Toast.LENGTH_LONG).show()
            }
        })

        post_title?.text = post.title
        post_text?.text = post.description
        post_date?.text = Utils.displayedDate(post.date)

        // view links contained in description

        Linkify.addLinks(post_text, Linkify.ALL)
        Utils.convertToLinkSpan(this@PostActivity, post_text)

        // adapter

        this.adapter = CommentRecyclerViewAdapter(post.comments, requestManager, post.id)

        // edit comment

        comment_post_input.setupComponent(adapter, post)

        // recycler view

        recyclerview_comments_post.setHasFixedSize(true)
        recyclerview_comments_post.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview_comments_post.layoutManager = layoutManager

        recyclerview_comments_post.adapter = adapter

        // retrieve the avatar of the user

        val id = resources.getIdentifier(Utils.drawableProfileName(user?.promotion, user?.gender), "drawable", packageName)
        requestManager
            .load(id)
            .transition(withCrossFade())
            .apply(RequestOptions.circleCropTransform())
            .into(comment_post_username_avatar)

        // image

        if (post.image.isNotEmpty()) {
            post_placeholder?.setImageSize(post.imageSize)

            requestManager
                .load(ServiceGenerator.CDN_URL + post.image)
                .transition(withCrossFade())
                .into(post_image)
        }
    }

    override fun finish() {
        if (::post.isInitialized) {
            val sendIntent = Intent()
            sendIntent.putExtra("post", post)
            setResult(Activity.RESULT_OK, sendIntent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }

        super.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isTaskRoot) {
                    startActivity(Intent(this@PostActivity, MainActivity::class.java))
                } else {
                    finish()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val TAG = "PostActivity"
    }
}

fun Post.isPostLikedBy(userID: String): Boolean {
    for (idUser in likes)
        if (idUser == userID) return true

    return false
}