package fr.insapp.insapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.adapters.AttendeeRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;

/**
 * Created by thoma on 10/12/2016.
 */

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeRecyclerViewAdapter adapter;

    private ArrayList<String> attendees;
    private ArrayList<String> maybe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        Intent intent = getIntent();
        this.attendees = intent.getStringArrayListExtra("attendees");
        this.maybe = intent.getStringArrayListExtra("maybe");

        generateUsers(attendees, Event.PARTICIPATE.YES);
        generateUsers(maybe, Event.PARTICIPATE.MAYBE);

        this.adapter = new AttendeeRecyclerViewAdapter(this, true);
        adapter.setOnItemClickListener(new AttendeeRecyclerViewAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(User user) {
                startActivity(new Intent(getBaseContext(), ProfileActivity.class).putExtra("user", user));
            }
        });

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_users);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // recycler view

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void generateUsers(List<String> users, final Event.PARTICIPATE action) {
        for (int i = 0 ; i < users.size(); i++) {
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    JSONObject json;
                    try {
                        json = new JSONObject(output);
                        final User user = new User(json);

                        adapter.addItem(user, action);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            /*
            request.execute(HttpGet.ROOTUSER + "/" + users.get(i) + "?token=" + HttpGet.sessionCredentials.getSessionToken());
            */
        }
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
