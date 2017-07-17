package fr.insapp.insapp.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
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
 * Created by thomas on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    public static final int NOTIFICATION_MESSAGE = 10;

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

    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        this.clubAvatarCircleImageView = (CircleImageView) findViewById(R.id.post_club_avatar);
        this.titleTextView = (TextView) findViewById(R.id.post_title);
        this.descriptionTextView = (TextView) findViewById(R.id.post_text);
        this.dateTextView = (TextView) findViewById(R.id.post_date);
        this.userAvatarCircleImageView = (CircleImageView) findViewById(R.id.comment_post_username_avatar);

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
            onActivityResult(NOTIFICATION_MESSAGE, RESULT_OK, null);
        }
        else {
            generateActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTIFICATION_MESSAGE) {
            if (resultCode == RESULT_OK) {
                Call<Post> call = ServiceGenerator.create().getPostFromId(notification.getContent());
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                        if (response.isSuccessful()) {
                            post = response.body();
                            generateActivity();
                        }
                        else {
                            Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                        Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void generateActivity() {
        Call<Club> call = ServiceGenerator.create().getClubFromId(post.getAssociation());
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    club = response.body();

                    Glide
                            .with(PostActivity.this)
                            .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
                            .crossFade()
                            .into(clubAvatarCircleImageView);

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
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
            }
        });

        this.titleTextView.setText(post.getTitle());
        this.descriptionTextView.setText(post.getDescription());
        this.dateTextView.setText(String.format(getResources().getString(R.string.ago), Operation.displayedDate(post.getDate())));

        // view links contained in description

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(PostActivity.this, descriptionTextView);

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(PostActivity.this, post.getComments());
        adapter.setOnItemLongClickListener(new PostCommentLongClickListener(PostActivity.this, post, adapter));

        // edit comment

        CommentEditText commentEditText = (CommentEditText) findViewById(R.id.comment_post_input);
        commentEditText.setupComponent(adapter, post);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        // retrieve the avatar of the user

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
        final User user = gson.fromJson(getSharedPreferences("User", MODE_PRIVATE).getString("user", ""), User.class);

        final int id = getResources().getIdentifier(Operation.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", getPackageName());
        Glide
                .with(PostActivity.this)
                .load(id)
                .crossFade()
                .into(userAvatarCircleImageView);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTaskRoot()) {
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                }
                else {
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
