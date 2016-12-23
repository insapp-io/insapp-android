package fr.insapp.insapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thomas on 15/12/2016.
 */

public class ProfileActivity extends AppCompatActivity {

    private EventRecyclerViewAdapter adapter;

    private RecyclerView recyclerView;
    private CircleImageView avatar_profil;
    private TextView username;
    private TextView name;
    private TextView email;
    private TextView promo;
    private TextView description;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.avatar_profil = (CircleImageView) findViewById(R.id.profil_avatar);
        this.username = (TextView) findViewById(R.id.profile_username);
        this.name = (TextView) findViewById(R.id.profile_name);
        this.email = (TextView) findViewById(R.id.profile_email);
        this.promo = (TextView) findViewById(R.id.profile_class);
        this.description = (TextView) findViewById(R.id.profile_description);

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }

        // adapter

        this.adapter = new EventRecyclerViewAdapter(this, R.layout.row_event_with_avatars);
        adapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivity(new Intent(getBaseContext(), EventActivity.class));
            }
        });

        // recycler view

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_events_participate);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // Fill the main layout

        Resources resources = ProfileActivity.this.getResources();

        Intent intent = getIntent();
        if(intent.hasExtra("user")){
            this.user = intent.getParcelableExtra("user");

            int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", ProfileActivity.this.getPackageName());
            Drawable dr = ContextCompat.getDrawable(ProfileActivity.this, id);

            this.avatar_profil.setImageDrawable(dr);
            this.username.setText(user.getUsername());
            this.name.setText(user.getName());
            this.email.setText(user.getEmail());
            this.promo.setText(user.getPromotion());
            this.description.setText(user.getDescription());

            //
            if(user.getName().isEmpty())
                this.name.setVisibility(View.GONE);
            if(user.getEmail().isEmpty())
                this.email.setVisibility(View.GONE);
            if(user.getPromotion().isEmpty())
                this.promo.setVisibility(View.GONE);
        }
        else{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            int id = resources.getIdentifier(Operation.drawableProfilName(preferences.getString("class", ""), preferences.getString("sex", "")), "drawable", ProfileActivity.this.getPackageName());
            Drawable dr = ContextCompat.getDrawable(ProfileActivity.this, id);

            this.avatar_profil.setImageDrawable(dr);
            this.username.setText(HttpGet.credentials.getUsername());
            this.name.setText(preferences.getString("name", ""));
            this.email.setText(preferences.getString("email", ""));
            this.promo.setText(preferences.getString("class", ""));
            this.description.setText(preferences.getString("description", ""));

            if(user.getName().isEmpty())
                this.name.setVisibility(View.GONE);
            if(user.getEmail().isEmpty())
                this.email.setVisibility(View.GONE);
            if(user.getPromotion().isEmpty())
                this.promo.setVisibility(View.GONE);
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
