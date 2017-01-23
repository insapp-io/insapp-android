package fr.insapp.insapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;

    private Post post = null;
    private Club club = null;

    private CircleImageView avatar_club;
    private TextView title;
    private TextView description;
    private TextView date;

    private FloatingActionButton fab;

    // tags

    private ArrayList<Tag> tags = new ArrayList<>();

    private boolean userWrittingTag = false;
    private int tagStartsAt = 0;
    private String tagWritting = "";
    private boolean autochange = false;
    private boolean deleteTag = false;
    private int lastCount = 0;

    private AlertDialog alertDialog;
    private EditText editText;
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        this.avatar_club = (CircleImageView) findViewById(R.id.club_avatar_post);
        this.title = (TextView) findViewById(R.id.post_title);
        this.description = (TextView) findViewById(R.id.post_text);
        this.date = (TextView) findViewById(R.id.post_date);

        Intent intent = getIntent();
        this.post = intent.getParcelableExtra("post");

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // fill post elements

        this.club = HttpGet.clubs.get(post.getAssociation());

        Glide.with(getApplicationContext()).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(this.avatar_club);

        this.title.setText(post.getTitle());
        this.description.setText(post.getDescription());
        this.date.setText(new String("il y a " + Operation.displayedDate(post.getDate())));

        // description links

        Linkify.addLinks(description, Linkify.ALL);
        Utils.stripUnderlines(description);

        // listener

        this.avatar_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, ClubActivity.class).putExtra("club", club));
            }
        });

        // recycler view

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(PostActivity.this, post.getComments());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new CommentRecyclerViewAdapter.OnCommentItemLongClickListener() {
            @Override
            public void onCommentItemLongClick(final Comment comment) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostActivity.this);

                // delete comment
                if (HttpGet.credentials.getId().equalsIgnoreCase(comment.getUserId())) {
                    alertDialogBuilder.setTitle(getString(R.string.delete_comment_action));
                    alertDialogBuilder
                            .setMessage(R.string.delete_comment_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    setResult(RESULT_OK);

                                    HttpDelete delete = new HttpDelete(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                post = new Post(new JSONObject(output));
                                                adapter.setComments(post.getComments());

                                                //Toast.makeText(PostActivity.this, getString(R.string.delete_comment_success), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    delete.execute(HttpGet.ROOTPOST + "/" + post.getId() + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());
                                }
                            })
                            .setNegativeButton(getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    dialogAlert.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                // report comment
                else {
                    alertDialogBuilder.setTitle(getString(R.string.report_comment_action));
                    alertDialogBuilder
                            .setMessage(R.string.report_comment_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    HttpPut report = new HttpPut(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            Toast.makeText(PostActivity.this, getString(R.string.report_comment_success), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    report.execute(HttpGet.ROOTURL + "/report/" + post.getId() + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());
                                }
                            })
                            .setNegativeButton(getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    dialogAlert.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        // edit text

        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = params.rightMargin = 60;

        this.editText = new EditText(PostActivity.this);
        editText.setLayoutParams(params);

        final FrameLayout container = new FrameLayout(PostActivity.this);
        container.addView(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // skip execution if triggered by code
                if (autochange) {
                    //last_count = s.length();
                    autochange = false; // next change is not triggered by code
                    return;
                }

                if (charSequence.length() == 0) {
                    lastCount = 0;
                    userWrittingTag = false;
                    tagWritting = "";
                    tagStartsAt = 0;
                }

                // deletion
                if (charSequence.length() - lastCount < 0) {
                    if (userWrittingTag) {
                        String currentStr = editText.getText().toString();
                        String strWithoutTag = currentStr.substring(0, tagStartsAt) + currentStr.substring(tagStartsAt + tagWritting.length(), currentStr.length());

                        autochange = true;
                        editText.setText(strWithoutTag);
                        editText.setSelection(tagStartsAt);

                        lastCount = strWithoutTag.length();
                        userWrittingTag = false;
                        tagWritting = "";
                        tagStartsAt = 0;

                        deleteTag = true;
                    }
                }
                // writing
                else {
                    deleteTag = false;

                    int pos = editText.getSelectionStart() - 1;
                    if (pos >= 0) {
                        char c = charSequence.charAt(pos);

                        if (userWrittingTag) {
                            if (c == ' ' || pos <= tagStartsAt || pos - 1 > tagStartsAt + tagWritting.length()) {
                                userWrittingTag = false;
                            }
                            else {
                                tagWritting += charSequence.toString().charAt(pos);
                                showUsersToTag(tagWritting);
                            }
                        }
                        else {
                            if (c == '@') {
                                userWrittingTag = true;
                                tagStartsAt = pos;
                                tagWritting = "";
                            }
                        }
                    }
                }

                if (!deleteTag)
                    lastCount = charSequence.length();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // alert dialog

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.write_comment));
        alertDialogBuilder
                .setView(container)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.publish_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert, int id) {
                        final String text = ((EditText) container.getChildAt(0)).getText().toString();
                        if (!text.isEmpty()) {
                            final JSONObject json = new JSONObject();

                            try {
                                json.put("user", HttpGet.credentials.getUserID());
                                json.put("content", text);

                                JSONArray jsonArray = new JSONArray();
                                List<String> already_tagged = new ArrayList<>();
                                for (Tag tag : tags) {
                                    // if the user didn't delete it
                                    System.out.println("1 TAG" + tag.getName());
                                    if (text.contains(tag.getName()) && already_tagged.lastIndexOf(tag.getName()) == -1) {

                                        System.out.println("Valide: " + tag.getName());
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
                                        post = new Post(new JSONObject(output));
                                        adapter.setComments(post.getComments());

                                        ((EditText) container.getChildAt(0)).getText().clear();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            request.execute(HttpGet.ROOTPOST + "/" + post.getId() + "/comment?token=" + HttpGet.credentials.getSessionToken(), json.toString());

                            //setResult(RESULT_OK);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert, int id) {
                        dialogAlert.cancel();
                    }
                });

        this.alertDialog = alertDialogBuilder.create();

        // floating action button

        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        // popup menu

        this.popup = new PopupMenu(PostActivity.this, editText);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

        // result request

        //setResult(RESULT_CANCELED);
    }

    private void showUsersToTag(String username) {
        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject json = new JSONObject(output);
                    JSONArray jsonArray = json.getJSONArray("users");

                    if (jsonArray != null) {
                        popup.getMenu().clear();

                        for (int i = jsonArray.length() - 1; i >= jsonArray.length() - Math.min(jsonArray.length(), 3); i--) {
                            final User user = new User(jsonArray.getJSONObject(i));

                            popup.getMenu().add(Menu.NONE, Menu.NONE, i + 1, "@" + user.getUsername());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    String currentStr = editText.getText().toString();
                                    String strWithTag = currentStr.substring(0, tagStartsAt) + item.getTitle() + " " + currentStr.substring(tagStartsAt + tagWritting.length() + 1, currentStr.length());

                                    tags.add(new Tag("", user.getId(), "@" + user.getUsername()));

                                    autochange = true;
                                    editText.setText(strWithTag);
                                    editText.setSelection(tagStartsAt + user.getUsername().length() + 1);

                                    userWrittingTag = false;
                                    tagWritting = "";
                                    tagStartsAt = 0;

                                    return true;
                                }
                            });
                        }
                    }
                }
                catch (JSONException e) {
                    userWrittingTag = false;
                    tagWritting = "";
                    tagStartsAt = 0;

                    popup.dismiss();

                    e.printStackTrace();
                }

                popup.show();
            }
        });
        request.execute(HttpGet.ROOTSEARCHUSERS + "/" + username + "?token=" + HttpGet.credentials.getSessionToken());
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

                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
