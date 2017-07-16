package fr.insapp.insapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.AttendeeRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 10/12/2016.
 */

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeRecyclerViewAdapter adapter;

    private List<String> attendees;
    private List<String> maybe;

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
            Call<User> call = ServiceGenerator.create().getUserFromId(users.get(i));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        adapter.addItem(response.body(), action);
                    }
                    else {
                        Toast.makeText(AttendeesActivity.this, "AttendeesActivity", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Toast.makeText(AttendeesActivity.this, "AttendeesActivity", Toast.LENGTH_LONG).show();
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
