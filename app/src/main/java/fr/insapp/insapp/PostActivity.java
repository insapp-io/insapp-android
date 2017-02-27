package fr.insapp.insapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.listeners.PostCommentLongClickListener;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.utility.CommentEditText;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;

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
    private PopupMenu popup;

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

            if (HttpGet.credentials != null)
                onActivityResult(NOTIFICATION_MESSAGE, RESULT_OK, null);
            else
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), NOTIFICATION_MESSAGE);
        }
        else
            generateActivity();

        // comment user avatar

        this.userAvatarCircleImageView = (CircleImageView) findViewById(R.id.comment_post_username_avatar);

        // get the drawable of avatar

        Resources resources = getResources();
        final int id = resources.getIdentifier(Operation.drawableProfilName(MainActivity.getUser().getPromotion(), MainActivity.getUser().getGender()), "drawable", getPackageName());
        Glide.with(PostActivity.this).load(id).into(userAvatarCircleImageView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTIFICATION_MESSAGE) {
            if (resultCode == RESULT_OK) {
                HttpGet request = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            post = new Post(new JSONObject(output));

                            generateActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                request.execute(HttpGet.ROOTPOST + "/" + notification.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
            }
        }
    }

    public void generateActivity() {
        // fill post elements

        this.club = HttpGet.clubs.get(post.getAssociation());
        if (this.club == null) {
            HttpGet asso = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        club = new Club(new JSONObject(output));
                        HttpGet.clubs.put(club.getId(), club);

                        Glide.with(getApplicationContext()).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(clubAvatarCircleImageView);

                        // listener
                        clubAvatarCircleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(PostActivity.this, ClubActivity.class).putExtra("club", club));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            asso.execute(HttpGet.ROOTASSOCIATION + "/" + post.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());
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

        // popup menu

        this.popup = new PopupMenu(getApplicationContext(), commentEditText);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

        // edit text

        this.commentEditText = (CommentEditText) findViewById(R.id.comment_post_input);
        commentEditText.setTextChangedListener(popup);

        commentEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    final String text = commentEditText.getText().toString();
                    if (!text.isEmpty()) {
                        final JSONObject json = new JSONObject();

                        try {
                            json.put("user", HttpGet.credentials.getUserID());
                            json.put("content", text);

                            JSONArray jsonArray = new JSONArray();
                            List<String> already_tagged = new ArrayList<>();
                            for (final Tag tag : commentEditText.getTags()) {
                                // if the user didn't delete it
                                if (text.contains(tag.getName()) && already_tagged.lastIndexOf(tag.getName()) == -1) {
                                    JSONObject jsonTag = new JSONObject();
                                    jsonTag.put("user", tag.getUser());
                                    jsonTag.put("name", tag.getName());

                                    jsonArray.put(jsonTag);
                                    already_tagged.add(tag.getName());
                                }
                            }
                            json.put("tags", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                try {
                                    post.refresh(new JSONObject(output));
                                    adapter.setComments(post.getComments());

                                    commentEditText.getText().clear();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

                        request.execute(HttpGet.ROOTPOST + "/" + post.getId() + "/comment?token=" + HttpGet.credentials.getSessionToken(), json.toString());
                    }

                    return true;
                }

                return false;
            }
        });
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
                if (isTaskRoot()) {
                    Intent i = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else
                    finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
