package fr.insapp.insapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.ClubRecyclerViewAdapter;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter;
import fr.insapp.insapp.adapters.UserRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.SearchTerms;
import fr.insapp.insapp.models.UniversalSearchResults;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 11/12/2016.
 */

public class SearchActivity extends AppCompatActivity {

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
            this.query = intent.getStringExtra(SearchManager.QUERY);
        }

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // clubs recycler view

        RecyclerView recyclerViewClubs = (RecyclerView) findViewById(R.id.recyclerview_search_clubs);
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

        RecyclerView recyclerViewPosts = (RecyclerView) findViewById(R.id.recyclerview_search_posts);
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

        RecyclerView recyclerViewEvents = (RecyclerView) findViewById(R.id.recyclerview_search_events);
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

        RecyclerView recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerview_search_users);
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

        generateSearchResults();
    }

    private void generateSearchResults() {
        if (this.query != null) {
            adapterClubs.getClubs().clear();
            adapterPosts.getPosts().clear();
            adapterEvents.getEvents().clear();
            adapterUsers.getUsers().clear();

            Call<UniversalSearchResults> call = ServiceGenerator.create().universalSearch(new SearchTerms(query));
            call.enqueue(new Callback<UniversalSearchResults>() {
                @Override
                public void onResponse(@NonNull Call<UniversalSearchResults> call, @NonNull Response<UniversalSearchResults> response) {
                    if (response.isSuccessful()) {
                        final UniversalSearchResults results = response.body();

                        if (results.getClubs() != null) {
                            for (int i = 0; i < results.getClubs().size(); i++) {
                                final Club club = results.getClubs().get(i);

                                if (!club.getProfilPicture().isEmpty() && !club.getCover().isEmpty()) {
                                    adapterClubs.addItem(club);
                                    findViewById(R.id.search_clubs_layout).setVisibility(LinearLayout.VISIBLE);
                                }
                            }
                        }

                        if (results.getPosts() != null) {
                            for (int i = 0; i < results.getPosts().size(); i++) {
                                adapterPosts.addItem(results.getPosts().get(i));
                                findViewById(R.id.search_posts_layout).setVisibility(LinearLayout.VISIBLE);
                            }
                        }

                        if (results.getEvents() != null) {
                            final Date atm = Calendar.getInstance().getTime();

                            for (int i = 0; i < results.getEvents().size(); i++) {
                                final Event event = results.getEvents().get(i);

                                if (event.getDateEnd().getTime() > atm.getTime()) {
                                    adapterEvents.addItem(event);
                                    findViewById(R.id.search_events_layout).setVisibility(LinearLayout.VISIBLE);
                                }
                            }
                        }

                        if (results.getUsers() != null) {
                            for (int i = 0; i < results.getUsers().size(); i++) {
                                adapterUsers.addItem(results.getUsers().get(i));
                                findViewById(R.id.search_users_layout).setVisibility(LinearLayout.VISIBLE);
                            }
                        }
                    }
                    else {
                        Toast.makeText(SearchActivity.this, "SearchActivity", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UniversalSearchResults> call, @NonNull Throwable t) {
                    Toast.makeText(SearchActivity.this, "SearchActivity", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}