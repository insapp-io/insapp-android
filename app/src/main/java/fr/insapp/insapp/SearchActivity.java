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
                    System.out.println(output);
                }
            });
            request.execute(HttpGet.ROOTSEACHUNIVERSAL + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());
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

        generateClubs(adapterClubs, query);
        generatePosts(adapterPosts, query);
        generateEvents(adapterEvents, query);
        generateUsers(adapterUsers, query);
    }

    private void generateClubs(final ClubRecyclerViewAdapter adapter, String query) {
        adapterClubs.getClubs().clear();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("{\"associations\":null}")) {
                    try {

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("associations");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            final JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Club club = new Club(jsonobject);

                            if (!club.getProfilPicture().isEmpty() && !club.getCover().isEmpty()) {
                                adapter.addItem(club);
                                findViewById(R.id.search_clubs_layout).setVisibility(LinearLayout.VISIBLE);

                                // Add club to the list if it is new
                                Club c = HttpGet.clubs.get(club.getId());
                                if (c == null)
                                    HttpGet.clubs.put(club.getId(), club);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTSEARCHASSOCIAITIONS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());

        adapterClubs.notifyDataSetChanged();
    }

    private void generatePosts(final PostRecyclerViewAdapter adapter, String query) {
        adapterPosts.getPosts().clear();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("{\"posts\":null}")) {
                    try {

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("posts");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            adapter.addItem(new Post(jsonobject));
                            findViewById(R.id.search_posts_layout).setVisibility(LinearLayout.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTSEARCHPOSTS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());

        adapterPosts.notifyDataSetChanged();
    }

    private void generateEvents(final EventRecyclerViewAdapter adapter, String query) {
        adapterEvents.getEvents().clear();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("{\"events\":null}")) {

                    Date atm = Calendar.getInstance().getTime();
                    try {

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("events");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject = jsonarray.getJSONObject(i);

                            Event event = new Event(jsonObject);
                            if (event.getDateEnd().getTime() > atm.getTime()) {
                                adapter.addItem(event);
                                findViewById(R.id.search_events_layout).setVisibility(LinearLayout.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTSEARCHEVENTS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());

        adapterEvents.notifyDataSetChanged();
    }

    private void generateUsers(final UserRecyclerViewAdapter adapter, String query) {
        adapterUsers.getUsers().clear();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("{\"users\":null}")) {
                    try {

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("users");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject = jsonarray.getJSONObject(i);

                            adapter.addItem(new User(jsonObject));
                            findViewById(R.id.search_users_layout).setVisibility(LinearLayout.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTSEARCHUSERS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());

        adapterUsers.notifyDataSetChanged();
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