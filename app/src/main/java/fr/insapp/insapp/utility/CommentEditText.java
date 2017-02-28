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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.AutoCompleterAdapter;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;

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

    public void setupComponent(final HttpPost request, final String params) {
        setThreshold(2);

        this.adapter = new AutoCompleterAdapter(getContext(), R.id.comment_event_input);
        setAdapter(adapter);

        this.tokenizer = new ProfileTagTokenizer();
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
                        final JSONObject json = new JSONObject();

                        try {
                            json.put("user", HttpGet.credentials.getUserID());
                            json.put("content", text);

                            JSONArray jsonArray = new JSONArray();
                            List<String> alreadyTagged = new ArrayList<>();
                            for (final Tag tag : getTags()) {
                                if (text.contains(tag.getName()) && alreadyTagged.lastIndexOf(tag.getName()) == -1) {
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

                        request.execute(params, json.toString());
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
