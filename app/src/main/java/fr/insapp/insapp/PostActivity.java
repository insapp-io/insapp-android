package fr.insapp.insapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.listeners.PostCommentLongClickListener;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.CommentEditText;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    public static final int NOTIFICATION_MESSAGE = 10;

    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;

    private Post post;
    private Club club;

    // post

    private CircleImageView clubAvatarCircleImageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;

    // comment

    private CircleImageView userAvatarCircleImageView;
    private CommentEditText commentEditText;

    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        this.clubAvatarCircleImageView = (CircleImageView) findViewById(R.id.post_club_avatar);
        this.titleTextView = (TextView) findViewById(R.id.post_title);
        this.descriptionTextView = (TextView) findViewById(R.id.post_text);
        this.dateTextView = (TextView) findViewById(R.id.post_date);

        // post

        Intent intent = getIntent();
        this.post = intent.getParcelableExtra("post");

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Drawable upArrow = ContextCompat.getDrawable(PostActivity.this, R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        // if we come from an android notification
        if (this.post == null) {
            notification = intent.getParcelableExtra("notification");

            if (HttpGet.sessionCredentials != null)
                onActivityResult(NOTIFICATION_MESSAGE, RESULT_OK, null);
            else
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), NOTIFICATION_MESSAGE);
        }
        else
            generateActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTIFICATION_MESSAGE) {
            if (resultCode == RESULT_OK) {
                /*
                Call<Post> call = ServiceGenerator.createService(Client.class).getPostFromId(notification.getContent(), HttpGet.sessionCredentials.getSessionToken());
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if (response.isSuccessful()) {
                            post = response.body();

                            generateActivity();
                        }
                        else {
                            Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                    }
                });
                */
            }
        }
    }

    public void generateActivity() {
        // fill post elements

        this.club = HttpGet.clubs.get(post.getAssociation());
        if (this.club == null) {
            generateActivity();

            /*
            Call<Club> call = ServiceGenerator.createService(Client.class).getClubFromId(post.getAssociation(), HttpGet.sessionCredentials.getSessionToken());
            call.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(Call<Club> call, Response<Club> response) {
                    if (response.isSuccessful()) {
                        club = response.body();
                        HttpGet.clubs.put(club.getId(), club);

                        Glide.with(getApplicationContext()).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(clubAvatarCircleImageView);

                        // listener

                        clubAvatarCircleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(PostActivity.this, ClubActivity.class).putExtra("club", club));
                            }
                        });
                    }
                    else {
                        Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Club> call, Throwable t) {
                    Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                }
            });
            */
        }
        else {
            Glide.with(getApplicationContext()).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(this.clubAvatarCircleImageView);

            // listener
            this.clubAvatarCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PostActivity.this, ClubActivity.class).putExtra("club", club));
                }
            });
        }

        this.titleTextView.setText(post.getTitle());
        this.descriptionTextView.setText(post.getDescription());
        this.dateTextView.setText(String.format(getResources().getString(R.string.ago), Operation.displayedDate(post.getDate())));

        // description text view links

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(PostActivity.this, descriptionTextView);

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(PostActivity.this, post.getComments());
        adapter.setOnItemLongClickListener(new PostCommentLongClickListener(PostActivity.this, post, adapter));

        // recycler view

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        // retrieve the avatar of the user

        /*
        Call<User> call2 = ServiceGenerator.createService(Client.class).getUser(HttpGet.sessionCredentials.getUserID(), HttpGet.sessionCredentials.getSessionToken());
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    MainActivity.user = response.body();

                    final int id = getResources().getIdentifier(Operation.drawableProfilName(MainActivity.getUser().getPromotion(), MainActivity.getUser().getGender()), "drawable", getPackageName());
                    Glide.with(PostActivity.this).load(id).into(userAvatarCircleImageView);
                }
                else {
                    Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
            }
        });
        */

        // edit text

        this.commentEditText = (CommentEditText) findViewById(R.id.comment_post_input);
        commentEditText.setupComponent(adapter, post);

        // comment user avatar

        this.userAvatarCircleImageView = (CircleImageView) findViewById(R.id.comment_post_username_avatar);

        // get the drawable of avatar

        if (MainActivity.getUser() == null) {
            /*
            Call<User> call3 = ServiceGenerator.createService(Client.class).getUser(HttpGet.sessionCredentials.getUserID(), HttpGet.sessionCredentials.getSessionToken());
            call3.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        MainActivity.user = response.body();

                        final int id = getResources().getIdentifier(Operation.drawableProfilName(MainActivity.getUser().getPromotion(), MainActivity.getUser().getGender()), "drawable", getPackageName());
                        Glide.with(PostActivity.this).load(id).into(userAvatarCircleImageView);
                    }
                    else {
                        Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                }
            });
            */
        }
        else {
            final int id = getResources().getIdentifier(Operation.drawableProfilName(MainActivity.getUser().getPromotion(), MainActivity.getUser().getGender()), "drawable", getPackageName());
            Glide.with(PostActivity.this).load(id).into(userAvatarCircleImageView);
        }
    }

    @Override
    public void finish() {
        Intent sendIntent = new Intent();
        sendIntent.putExtra("post", post);

        setResult(RESULT_OK, sendIntent);

        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (isTaskRoot())
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                else
                    finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
