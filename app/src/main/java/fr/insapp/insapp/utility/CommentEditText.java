package fr.insapp.insapp.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.AutoCompleterAdapter;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 27/02/2017.
 */

public class CommentEditText extends MultiAutoCompleteTextView {

    private AutoCompleterAdapter adapter;
    private Tokenizer tokenizer;

    // tags

    private ArrayList<Tag> tags = new ArrayList<>();

    public CommentEditText(Context context) {
        super(context);
    }

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setupComponent(final CommentRecyclerViewAdapter commentAdapter, final Post post) {
        setThreshold(2);

        this.adapter = new AutoCompleterAdapter(getContext(), R.id.comment_event_input);
        setAdapter(adapter);

        this.tokenizer = new TagTokenizer();
        setTokenizer(tokenizer);

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String itemString = (((TextView) view.findViewById(R.id.dropdown_textview)).getText()).toString();

                String id = "";
                for (final User user : adapter.getTaggedUsers()) {
                    final String username = "@" + user.getUsername();
                    if (username.equals(itemString))
                        id = user.getId();
                }

                tags.add(new Tag("", id, itemString));
            }
        });

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    // hide keyboard

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    final String text = getText().toString();
                    getText().clear();

                    if (!text.isEmpty()) {
                        /*
                        final JSONObject json = new JSONObject();

                        try {
                            json.put("user", HttpGet.sessionCredentials.getUserID());
                            json.put("content", contentTextView);

                            JSONArray jsonArray = new JSONArray();
                            List<String> alreadyTagged = new ArrayList<>();
                            for (final Tag tag : getTags()) {
                                if (contentTextView.contains(tag.getName()) && alreadyTagged.lastIndexOf(tag.getName()) == -1) {
                                    JSONObject jsonTag = new JSONObject();
                                    jsonTag.put("user", tag.getUser());
                                    jsonTag.put("name", tag.getName());

                                    jsonArray.put(jsonTag);
                                    alreadyTagged.add(tag.getName());
                                }
                            }
                            json.put("tags", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println(json.toString());
                        */

                        /*
                        final Comment comment = new Comment(null, HttpGet.sessionCredentials.getUserID(), contentTextView, tags, null);
                        */

                        /*
                        Call<Post> call = ServiceGenerator.createService(Client.class).commentPost(post.getId(), comment, HttpGet.sessionCredentials.getSessionToken());
                        call.enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(Call<Post> call, Response<Post> response) {
                                if (response.isSuccessful()) {
                                    commentAdapter.setComments(response.body().getComments());

                                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.write_comment_success), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Post> call, Throwable t) {
                                Toast.makeText(getContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                            }
                        });
                        */
                    }

                    return true;
                }

                return false;
            }
        });
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
}
