package fr.insapp.insapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Post
import fr.insapp.insapp.utility.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_posts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 27/10/2016.
 */

class PostsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var layout: Int = 0
    private var filterAssociationId: String? = null
    private var swipeColor: Int = 0

    private lateinit var adapter: PostRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments

        val bundle = arguments
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.post_with_avatars)
            this.filterAssociationId = bundle.getString("filter_club_id")
            this.swipeColor = bundle.getInt("swipe_color")
        }

        // adapter

        this.adapter = PostRecyclerViewAdapter(mutableListOf(), Glide.with(this), layout)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view

        recyclerview_posts.setHasFixedSize(true)
        recyclerview_posts.isNestedScrollingEnabled = false

        if (layout == R.layout.post_with_avatars) {
            recyclerview_posts.addItemDecoration(DividerItemDecoration(resources, R.drawable.half_divider))
        } else if (layout == R.layout.post) {
            recyclerview_posts.addItemDecoration(DividerItemDecoration(resources, R.drawable.full_divider))
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_posts.layoutManager = layoutManager
        recyclerview_posts.adapter = adapter

        // swipe refresh layout

        refresh_posts.setOnRefreshListener(this)

        if (filterAssociationId != null) {
            refresh_posts.setColorSchemeColors(swipeColor)
        } else {
            refresh_posts.setColorSchemeResources(R.color.colorPrimary)
        }

        generatePosts()
    }

    private fun generatePosts() {
        no_network?.visibility = View.GONE

        val call = if (filterAssociationId == null) {
            ServiceGenerator.create().latestPosts
        } else {
            ServiceGenerator.create().getPostsForAssociation(filterAssociationId)
        }

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    adapter.posts.clear()
                    addPostsToAdapter(response.body())
                } else {
                    if (adapter.posts.isEmpty()) {
                        no_network?.visibility = View.VISIBLE
                    } else {
                        Snackbar.make(refresh_posts, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                    }
                }

                refresh_posts?.isRefreshing = false
                progress_bar?.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                if (adapter.posts.isEmpty()) {
                    no_network?.visibility = View.VISIBLE
                } else if (refresh_posts != null){
                    Snackbar.make(refresh_posts, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                }
                refresh_posts?.isRefreshing = false
                progress_bar?.visibility = View.GONE
            }
        })
    }

    private fun addPostsToAdapter(posts: List<Post>?) {
        if (posts != null) {
            for (post in posts) {
                adapter.addItem(post)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        //TODO: update the result of liking a post

        if (requestCode == POST_REQUEST) {
            /*
            when (resultCode) {
                RESULT_OK -> {
                    val post = intent!!.getParcelableExtra<Post>("post")

                    for (i in 0 until adapter.itemCount) {
                        if (adapter.posts[i].id == post.id) {
                            adapter.updatePost(i, post)
                        }
                    }
                }

                RESULT_CANCELED -> {
                }
                else -> {
                }
            }
            */
        }
    }

    override fun onRefresh() {
        generatePosts()
    }

    companion object {
        private const val POST_REQUEST = 3
    }
}