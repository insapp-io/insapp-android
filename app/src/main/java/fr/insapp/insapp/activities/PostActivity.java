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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.like.LikeButton;
import com.like.OnLikeListener;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.listeners.PostCommentLongClickListener;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Notifications;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.CommentEditText;
import fr.insapp.insapp.utility.RatioImageView;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thomas on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    private CommentRecyclerViewAdapter adapter;

    private Post post;
    private Club club;

    // post

    private CircleImageView clubAvatarCircleImageView;
    private TextView titleTextView;
    private LikeButton likeButton;
    private TextView likeCounterTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private CircleImageView userAvatarCircleImageView;
    private ImageView imageView;
    private RatioImageView placeholderImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // building ui

        this.clubAvatarCircleImageView = (CircleImageView) findViewById(R.id.post_club_avatar);
        this.titleTextView = (TextView) findViewById(R.id.post_title);
        this.likeButton = (LikeButton) findViewById(R.id.post_like_button);
        this.likeCounterTextView = (TextView) findViewById(R.id.post_like_counter);
        this.descriptionTextView = (TextView) findViewById(R.id.post_text);
        this.dateTextView = (TextView) findViewById(R.id.post_date);
        this.userAvatarCircleImageView = (CircleImageView) findViewById(R.id.comment_post_username_avatar);
        this.imageView = (ImageView) findViewById(R.id.post_image);
        this.placeholderImageView = (RatioImageView) findViewById(R.id.post_placeholder);

        // post

        Intent intent = getIntent();
        this.post = intent.getParcelableExtra("post");

        final User user = Utils.getUser();

        // Answers

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(post.getId())
                .putContentName(post.getTitle())
                .putContentType("Post")
                .putCustomAttribute("Favorites count", post.getLikes().size())
                .putCustomAttribute("Comments count", post.getComments().size()));

        // hide image if necessary

        if (post.getImageSize() == null || post.getImage().isEmpty()) {
            placeholderImageView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }

        // mark notification as seen

        if (intent.getParcelableExtra("notification") != null) {
            final Notification notification = intent.getParcelableExtra("notification");

            Call<Notifications> call = ServiceGenerator.create().markNotificationAsSeen(user.getId(), notification.getId());
            call.enqueue(new Callback<Notifications>() {
                @Override
                public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
                    Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                }
            });
        }

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.post_toolbar);
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

        // like button

        likeButton.setLiked(post.isPostLikedBy(user.getId()));
        likeCounterTextView.setText(post.getLikes().size() + "");

        likeButton.setOnLikeListener(new OnLikeListener() {
             @Override
             public void liked(LikeButton likeButton) {
                 likeCounterTextView.setText(Integer.valueOf((String) likeCounterTextView.getText()) + 1 + "");

                 Call<PostInteraction> call = ServiceGenerator.create().likePost(post.getId(), user.getId());
                 call.enqueue(new Callback<PostInteraction>() {
                     @Override
                     public void onResponse(@NonNull Call<PostInteraction> call, @NonNull Response<PostInteraction> response) {
                         if (response.isSuccessful()) {
                             post = response.body().getPost();
                         }
                         else {
                             Toast.makeText(PostActivity.this, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void onFailure(@NonNull Call<PostInteraction> call, @NonNull Throwable t) {
                         Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                     }
                 });
             }

             @Override
             public void unLiked(LikeButton likeButton) {
                 likeCounterTextView.setText(Integer.valueOf((String) likeCounterTextView.getText()) - 1 + "");

                 if (Integer.valueOf((String) likeCounterTextView.getText()) < 0) {
                     likeCounterTextView.setText("0");
                 }

                 Call<PostInteraction> call = ServiceGenerator.create().dislikePost(post.getId(), user.getId());
                 call.enqueue(new Callback<PostInteraction>() {
                     @Override
                     public void onResponse(@NonNull Call<PostInteraction> call, @NonNull Response<PostInteraction> response) {
                         if (response.isSuccessful()) {
                             post = response.body().getPost();
                         }
                         else {
                             Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void onFailure(@NonNull Call<PostInteraction> call, @NonNull Throwable t) {
                         Toast.makeText(PostActivity.this, "PostActivity", Toast.LENGTH_LONG).show();
                     }
                 });
             }
        });

        generateActivity();
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
                            .transition(withCrossFade())
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
        this.dateTextView.setText(Utils.displayedDate(post.getDate()));

        // view links contained in description

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(PostActivity.this, descriptionTextView);

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(PostActivity.this, Glide.with(this), post.getComments());
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

        final User user = Utils.getUser();

        final int id = getResources().getIdentifier(Utils.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", getPackageName());
        Glide
                .with(PostActivity.this)
                .load(id)
                .transition(withCrossFade())
                .into(userAvatarCircleImageView);

        // image

        if (post.getImageSize() != null && !post.getImage().isEmpty()) {
            placeholderImageView.setImageSize(post.getImageSize());

            Glide
                    .with(PostActivity.this)
                    .load(ServiceGenerator.CDN_URL + post.getImage())
                    .transition(withCrossFade())
                    .into(imageView);
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
