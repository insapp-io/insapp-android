package fr.insapp.insapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // query

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Recherche: " + query, Toast.LENGTH_SHORT).show();

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
        if(toolbar != null) {
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

        ClubRecyclerViewAdapter adapterClubs = new ClubRecyclerViewAdapter(this);
        recyclerViewClubs.setAdapter(adapterClubs);

        // posts recycler view

        this.recyclerViewPosts = (RecyclerView) findViewById(R.id.recyclerview_search_posts);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManagerPosts = new GridLayoutManager(this, 3);
        recyclerViewPosts.setLayoutManager(layoutManagerPosts);

        PostRecyclerViewAdapter adapterPosts = new PostRecyclerViewAdapter(this, R.layout.row_post_thumb);
        recyclerViewPosts.setAdapter(adapterPosts);

        // events recycler view

        this.recyclerViewEvents = (RecyclerView) findViewById(R.id.recyclerview_search_events);
        recyclerViewEvents.setHasFixedSize(true);
        recyclerViewEvents.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerEvents = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewEvents.setLayoutManager(layoutManagerEvents);

        EventRecyclerViewAdapter adapterEvents = new EventRecyclerViewAdapter(this, R.layout.row_event_with_avatars);
        recyclerViewEvents.setAdapter(adapterEvents);

        // users recycler view

        this.recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerview_search_users);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManagerUsers = new GridLayoutManager(this, 3);
        recyclerViewUsers.setLayoutManager(layoutManagerUsers);

        UserRecyclerViewAdapter adapterUsers = new UserRecyclerViewAdapter(this);
        recyclerViewUsers.setAdapter(adapterUsers);

        generateClubs(adapterClubs, query);
        generatePosts(adapterPosts, query);
        generateEvents(adapterEvents, query);
        generateUsers(adapterUsers, query);
    }

    private void generateClubs(final ClubRecyclerViewAdapter adapter, String query) {

        HttpGet request = new HttpGet(new AsyncResponse() {

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

                                // Add club to the list if it is new
                                Club c = HttpGet.clubs.get(club.getId());
                                if(c == null)
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
    }

    private void generatePosts(final PostRecyclerViewAdapter adapter, String query) {

        HttpGet request = new HttpGet(new AsyncResponse() {

            public void processFinish(String output) {

                if (!output.equals("{\"posts\":null}")) {
                    try {

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("posts");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            adapter.addItem(new Post(jsonobject));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTSEARCHPOSTS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());
    }

    private void generateEvents(final EventRecyclerViewAdapter adapter, String query) {

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
                            if (event.getDateEnd().getTime() > atm.getTime())
                                adapter.addItem(event);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTSEARCHEVENTS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());
    }

    private void  generateUsers(final UserRecyclerViewAdapter adapter, String query) {

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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTSEARCHUSERS + "/" + query + "?token=" + HttpGet.credentials.getSessionToken());
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