package fr.insapp.insapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.adapters.UserRecyclerViewAdapter;
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

        this.adapter = new UserRecyclerViewAdapter(this, generateUsers());
        adapter.setOnItemClickListener(new UserRecyclerViewAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(User user) {

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

    private List<User> generateUsers() {
        final List<User> users = new ArrayList<>();

        for (int i = 0; i < 56; i++) {
            users.add(new User("tbouvier", "tomatrocho"));
        }

        return users;
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
