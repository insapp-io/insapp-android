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

import fr.insapp.insapp.adapters.UserRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.User;

/**
 * Created by thoma on 10/12/2016.
 */

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private UserRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Intent intent = getIntent();
        List<String> users = intent.getStringArrayListExtra("users");

        this.adapter = new UserRecyclerViewAdapter(this, true);
        adapter.setOnItemClickListener(new UserRecyclerViewAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(User user) {
                startActivity(new Intent(getBaseContext(), ProfileActivity.class).putExtra("user", user));
            }
        });

        generateUsers(users);

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

    private void generateUsers(List<String> users) {

        for(int i=0; i<users.size(); i++) {

            final int id = i;
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(output);
                        final User user = new User(json);

                        adapter.addItem(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTUSER + "/" + users.get(i) + "?token=" + HttpGet.credentials.getSessionToken());
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
