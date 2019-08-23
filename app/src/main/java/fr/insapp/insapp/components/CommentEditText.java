package fr.insapp.insapp.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.AutoCompleterAdapter;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.TagTokenizer;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void setupComponent(final CommentRecyclerViewAdapter commentAdapter, final Object object) {
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

                        break;
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
                        final User user = Utils.INSTANCE.getUser();

                        final Comment comment = Comment.create(null, user.getId(), content, null, tags);

                        if (object instanceof Post) {
                            Call<Post> call = ServiceGenerator.create().commentPost(((Post) object).getId(), comment);
                            call.enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                    if (response.isSuccessful()) {
                                        commentAdapter.setComments(response.body().getComments());
                                        tags.clear();

                                        Toast.makeText(App.getAppContext(), getContext().getResources().getText(R.string.write_comment_success), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(App.getAppContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                                    Toast.makeText(App.getAppContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        else if (object instanceof Event) {
                            Call<Event> call = ServiceGenerator.create().commentEvent(((Event) object).getId(), comment);
                            call.enqueue(new Callback<Event>() {
                                @Override
                                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                                    if (response.isSuccessful()) {
                                        commentAdapter.setComments(response.body().getComments());
                                        tags.clear();

                                        Toast.makeText(App.getAppContext(), getContext().getResources().getText(R.string.write_comment_success), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(App.getAppContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                                    Toast.makeText(App.getAppContext(), "CommentEditText", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    return true;
                }

                return false;
            }
        });
    }
}
