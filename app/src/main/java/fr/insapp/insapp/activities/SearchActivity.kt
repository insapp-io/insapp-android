package fr.insapp.insapp.activities

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.SearchEvent
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.ClubRecyclerViewAdapter
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter
import fr.insapp.insapp.adapters.UserRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.SearchTerms
import fr.insapp.insapp.models.UniversalSearchResults
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 11/12/2016.
 */

class SearchActivity : AppCompatActivity() {

    private var adapterClubs: ClubRecyclerViewAdapter? = null
    private var adapterPosts: PostRecyclerViewAdapter? = null
    private var adapterEvents: EventRecyclerViewAdapter? = null
    private var adapterUsers: UserRecyclerViewAdapter? = null

    private var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // query

        val intent = intent
        if (Intent.ACTION_SEARCH == intent.action) {
            this.query = intent.getStringExtra(SearchManager.QUERY)

            // Answers

            Answers.getInstance().logSearch(SearchEvent().putQuery(query))
        }

        // toolbar

        setSupportActionBar(toolbar_search)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val requestManager = Glide.with(this)

        // clubs recycler view

        recyclerview_search_clubs.setHasFixedSize(true)
        recyclerview_search_clubs.isNestedScrollingEnabled = false

        val layoutManagerClubs = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_search_clubs.layoutManager = layoutManagerClubs

        this.adapterClubs = ClubRecyclerViewAdapter(this, requestManager, false)
        adapterClubs!!.setOnItemClickListener { club -> startActivity(Intent(baseContext, ClubActivity::class.java).putExtra("club", club)) }

        recyclerview_search_clubs.adapter = adapterClubs

        // posts recycler view

        val recyclerViewPosts = findViewById<View>(R.id.recyclerview_search_posts) as RecyclerView
        recyclerViewPosts.setHasFixedSize(true)
        recyclerViewPosts.isNestedScrollingEnabled = false

        val layoutManagerPosts = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewPosts.layoutManager = layoutManagerPosts

        this.adapterPosts = PostRecyclerViewAdapter(this, requestManager, R.layout.row_post)
        adapterPosts!!.setOnItemClickListener { post -> startActivity(Intent(baseContext, PostActivity::class.java).putExtra("post", post)) }

        recyclerViewPosts.adapter = adapterPosts

        // events recycler view

        val recyclerViewEvents = findViewById<View>(R.id.recyclerview_search_events) as RecyclerView
        recyclerViewEvents.setHasFixedSize(true)
        recyclerViewEvents.isNestedScrollingEnabled = false

        val layoutManagerEvents = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewEvents.layoutManager = layoutManagerEvents

        this.adapterEvents = EventRecyclerViewAdapter(this, requestManager, false, R.layout.row_event_with_avatars)
        adapterEvents!!.setOnItemClickListener { event -> startActivity(Intent(baseContext, EventActivity::class.java).putExtra("event", event)) }
        recyclerViewEvents.adapter = adapterEvents

        // users recycler view

        val recyclerViewUsers = findViewById<View>(R.id.recyclerview_search_users) as RecyclerView
        recyclerViewUsers.setHasFixedSize(true)
        recyclerViewUsers.isNestedScrollingEnabled = false

        val layoutManagerUsers = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUsers.layoutManager = layoutManagerUsers

        this.adapterUsers = UserRecyclerViewAdapter(this, requestManager, false)
        adapterUsers!!.setOnItemClickListener { user -> startActivity(Intent(baseContext, ProfileActivity::class.java).putExtra("user", user)) }

        recyclerViewUsers.adapter = adapterUsers

        // hide layouts

        search_clubs_layout?.visibility = LinearLayout.GONE
        search_posts_layout?.visibility  = LinearLayout.GONE
        search_events_layout?.visibility  = LinearLayout.GONE
        search_users_layout?.visibility = LinearLayout.GONE
        search_no_result?.visibility = LinearLayout.GONE

        // search

        generateSearchResults()
    }

    private fun generateSearchResults() {
        if (this.query != null) {
            adapterClubs!!.clubs.clear()
            adapterPosts!!.posts.clear()
            adapterEvents!!.events.clear()
            adapterUsers!!.users.clear()

            val call = ServiceGenerator.create().universalSearch(SearchTerms(query))
            call.enqueue(object : Callback<UniversalSearchResults> {
                override fun onResponse(call: Call<UniversalSearchResults>, response: Response<UniversalSearchResults>) {
                    if (response.isSuccessful) {
                        val results = response.body()

                        if (results!!.clubs != null) {
                            for (i in 0 until results.clubs.size) {
                                val club = results.clubs[i]

                                if (!club.profilePicture.isEmpty() && !club.cover.isEmpty()) {
                                    adapterClubs!!.addItem(club)
                                    search_clubs_layout.visibility = LinearLayout.VISIBLE
                                }
                            }
                        }

                        if (results.posts != null) {
                            for (i in 0 until results.posts.size) {
                                adapterPosts!!.addItem(results.posts[i])
                                search_posts_layout.visibility = LinearLayout.VISIBLE
                            }
                        }

                        if (results.events != null) {
                            val atm = Calendar.getInstance().time

                            for (i in 0 until results.events.size) {
                                val event = results.events[i]

                                if (event.dateEnd.time > atm.time) {
                                    adapterEvents!!.addItem(event)
                                    search_events_layout.visibility = LinearLayout.VISIBLE
                                }
                            }
                        }

                        if (results.users != null) {
                            for (i in 0 until results.users.size) {
                                adapterUsers!!.addItem(results.users[i])
                                search_users_layout.visibility = LinearLayout.VISIBLE
                            }
                        }

                        if(results.clubs == null && results.events == null && results.posts == null && results.users == null){
                            search_no_result?.visibility = LinearLayout.VISIBLE
                        }

                    } else {
                        Toast.makeText(this@SearchActivity, "SearchActivity", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<UniversalSearchResults>, t: Throwable) {
                    Toast.makeText(this@SearchActivity, "SearchActivity", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}