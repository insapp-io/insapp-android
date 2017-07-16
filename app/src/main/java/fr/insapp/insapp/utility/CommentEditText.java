package fr.insapp.insapp.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import auto.parcelgson.AutoParcelGson;
import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.AutoCompleterAdapter;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.Client;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 27/02/2017.
 */

public class CommentEditText extends AppCompatMultiAutoCompleteTextView {

    private AutoCompleterAdapter adapter;

    private List<Tag> tags = new ArrayList<>();

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
        setThreshold(1);
        setTokenizer(new TagTokenizer());

        this.adapter = new AutoCompleterAdapter(getContext(), R.id.comment_event_input);
        setAdapter(adapter);

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String itemString = (((TextView) view.findViewById(R.id.dropdown_textview)).getText()).toString();

                String userId = "";
                for (final User user : adapter.getFilteredUsers()) {
                    final String username = "@" + user.getUsername();
                    if (username.equals(itemString)) {
                        userId = user.getId();
                    }
                }

                tags.add(Tag.create(null, userId, itemString));
            }
        });

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    // hide keyboard

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    final String content = getText().toString();
                    getText().clear();

                    if (!content.isEmpty()) {
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
                        final User user = gson.fromJson(getContext().getSharedPreferences("Credentials", MODE_PRIVATE).getString("session", ""), SessionCredentials.class).getUser();
                        final Comment comment = Comment.create(null, user.getId(), content, null, tags);

                        Call<Post> call = ServiceGenerator.createService(Client.class).commentPost(post.getId(), comment);
                        call.enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                if (response.isSuccessful()) {
                                    commentAdapter.setComments(response.body().getComments());

                                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.write_comment_success), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                                Toast.makeText(getContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    return true;
                }

                return false;
            }
        });
    }

    public List<Tag> getTags() {
        return tags;
    }
}
