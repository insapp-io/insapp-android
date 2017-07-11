package fr.insapp.insapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.adapters.ClubRecyclerViewAdapter;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter;
import fr.insapp.insapp.adapters.UserRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;

/**
 * Created by thoma on 11/12/2016.
 */

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerViewClubs, recyclerViewPosts, recyclerViewEvents, recyclerViewUsers;
    private ClubRecyclerViewAdapter adapterClubs;
    private PostRecyclerViewAdapter adapterPosts;
    private EventRecyclerViewAdapter adapterEvents;

    private UserRecyclerViewAdapter adapterUsers;

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // query

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            /*
            request.execute(HttpGet.ROOTSEACHUNIVERSAL + "/" + query + "?token=" + HttpGet.sessionCredentials.getSessionToken());
            */
        }

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // clubs recycler view

        this.recyclerViewClubs = (RecyclerView) findViewById(R.id.recyclerview_search_clubs);
        recyclerViewClubs.setHasFixedSize(true);
        recyclerViewClubs.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerClubs = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewClubs.setLayoutManager(layoutManagerClubs);

        this.adapterClubs = new ClubRecyclerViewAdapter(this, false);
        adapterClubs.setOnItemClickListener(new ClubRecyclerViewAdapter.OnClubItemClickListener() {
            @Override
            public void onClubItemClick(Club club) {
                startActivity(new Intent(getBaseContext(), ClubActivity.class).putExtra("club", club));
            }
        });

        recyclerViewClubs.setAdapter(adapterClubs);

        // posts recycler view

        this.recyclerViewPosts = (RecyclerView) findViewById(R.id.recyclerview_search_posts);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerPosts = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPosts.setLayoutManager(layoutManagerPosts);

        this.adapterPosts = new PostRecyclerViewAdapter(this, R.layout.row_post);
        adapterPosts.setOnItemClickListener(new PostRecyclerViewAdapter.OnPostItemClickListener() {
            @Override
            public void onPostItemClick(Post post) {
                startActivity(new Intent(getBaseContext(), PostActivity.class).putExtra("post", post));
            }
        });

        recyclerViewPosts.setAdapter(adapterPosts);

        // events recycler view

        this.recyclerViewEvents = (RecyclerView) findViewById(R.id.recyclerview_search_events);
        recyclerViewEvents.setHasFixedSize(true);
        recyclerViewEvents.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerEvents = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewEvents.setLayoutManager(layoutManagerEvents);

        this.adapterEvents = new EventRecyclerViewAdapter(this, R.layout.row_event_with_avatars);
        adapterEvents.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivity(new Intent(getBaseContext(), EventActivity.class).putExtra("event", event));
            }
        });
        recyclerViewEvents.setAdapter(adapterEvents);

        // users recycler view

        this.recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerview_search_users);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerUsers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewUsers.setLayoutManager(layoutManagerUsers);

        this.adapterUsers = new UserRecyclerViewAdapter(this, false);
        adapterUsers.setOnItemClickListener(new UserRecyclerViewAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(User user) {
                startActivity(new Intent(getBaseContext(), ProfileActivity.class).putExtra("user", user));
            }
        });

        recyclerViewUsers.setAdapter(adapterUsers);

        // hide layouts

        findViewById(R.id.search_clubs_layout).setVisibility(LinearLayout.GONE);
        findViewById(R.id.search_posts_layout).setVisibility(LinearLayout.GONE);
        findViewById(R.id.search_events_layout).setVisibility(LinearLayout.GONE);
        findViewById(R.id.search_users_layout).setVisibility(LinearLayout.GONE);

        // search

        generate(query);
    }

    private void generate(String query) {
        adapterClubs.getClubs().clear();
        adapterPosts.getPosts().clear();
        adapterEvents.getEvents().clear();
        adapterUsers.getUsers().clear();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("terms", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpPost request = new HttpPost(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                    try {

                        JSONObject json = new JSONObject(output);

                        // CLUBS
                        JSONArray jsonarrayClubs = json.optJSONArray("associations");

                        if(jsonarrayClubs != null) {
                            for (int i = 0; i < jsonarrayClubs.length(); i++) {
                                final JSONObject jsonobject = jsonarrayClubs.getJSONObject(i);
                                Club club = new Club(jsonobject);

                                if (!club.getProfilPicture().isEmpty() && !club.getCover().isEmpty()) {
                                    adapterClubs.addItem(club);
                                    findViewById(R.id.search_clubs_layout).setVisibility(LinearLayout.VISIBLE);

                                    // Add club to the list if it is new
                                    Club c = HttpGet.clubs.get(club.getId());
                                    if (c == null)
                                        HttpGet.clubs.put(club.getId(), club);
                                }
                            }
                            adapterClubs.notifyDataSetChanged();
                        }

                        // POSTS
                        JSONArray jsonarrayPosts = json.optJSONArray("posts");
                        if(jsonarrayPosts != null) {

                            for (int i = 0; i < jsonarrayPosts.length(); i++) {
                                JSONObject jsonobject = jsonarrayPosts.getJSONObject(i);

                                adapterPosts.addItem(new Post(jsonobject));
                                findViewById(R.id.search_posts_layout).setVisibility(LinearLayout.VISIBLE);
                            }
                            adapterPosts.notifyDataSetChanged();
                        }

                        // EVENTS
                        Date atm = Calendar.getInstance().getTime();

                        JSONArray jsonarrayEvents = json.optJSONArray("events");
                        if(jsonarrayEvents != null) {

                            for (int i = 0; i < jsonarrayEvents.length(); i++) {
                                JSONObject jsonObject = jsonarrayEvents.getJSONObject(i);

                                Event event = new Event(jsonObject);
                                if (event.getDateEnd().getTime() > atm.getTime()) {
                                    adapterEvents.addItem(event);
                                    findViewById(R.id.search_events_layout).setVisibility(LinearLayout.VISIBLE);
                                }
                            }
                            adapterEvents.notifyDataSetChanged();
                        }

                        // users
                        JSONArray jsonarrayUsers = json.optJSONArray("users");
                        if (jsonarrayUsers != null) {

                            for (int i = 0; i < jsonarrayUsers.length(); i++) {
                                JSONObject jsonObject = jsonarrayUsers.getJSONObject(i);

                                adapterUsers.addItem(new User(jsonObject));
                                findViewById(R.id.search_users_layout).setVisibility(LinearLayout.VISIBLE);
                            }
                            adapterUsers.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        /*
        request.execute(HttpGet.ROOTSEACHUNIVERSAL + "?token=" + HttpGet.sessionCredentials.getSessionToken(), jsonObject.toString());
        */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}