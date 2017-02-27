package fr.insapp.insapp.utility;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;

/**
 * Created by thomas on 27/02/2017.
 */

public class CommentEditText extends EditText {

    // tags

    private ArrayList<Tag> tags = new ArrayList<>();
    private List<User> usersTagged = new ArrayList<>();

    private boolean userWrittingTag = false;
    private int tagStartsAt = 0;
    private String tagWritting = "";
    private boolean autochange = false;
    private boolean deleteTag = false;
    private int lastCount = 0;

    private PopupMenu popup;

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CommentEditText(Context context) {
        super(context);

    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTextChangedListener(PopupMenu popup) {
        this.popup = popup;

        addTextChangedListener(new TextWatcher() {
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
                        String currentStr = getText().toString();
                        String strWithoutTag = currentStr.substring(0, tagStartsAt) + currentStr.substring(tagStartsAt + tagWritting.length(), currentStr.length());

                        autochange = true;
                        setText(strWithoutTag);
                        setSelection(tagStartsAt);

                        lastCount = strWithoutTag.length();
                        userWrittingTag = false;
                        tagWritting = "";
                        tagStartsAt = 0;

                        deleteTag = true;
                        //popup.dismiss();
                    }
                }
                // writing
                else {
                    deleteTag = false;

                    int pos = getSelectionStart() - 1;
                    if (pos >= 0) {
                        final char c = charSequence.charAt(pos);
                        if (userWrittingTag) {
                            if (c == ' ' || pos <= tagStartsAt || pos - 1 > tagStartsAt + tagWritting.length()) {
                                userWrittingTag = false;
                            } else {
                                tagWritting += charSequence.toString().charAt(pos);
                                showUsersToTag(tagWritting);
                            }
                        } else {
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
    }

    private void showUsersToTag(String username) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("terms", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpPost request = new HttpPost(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject json = new JSONObject(output);
                    JSONArray jsonArray = json.getJSONArray("users");

                    if (jsonArray != null) {
                        popup.getMenu().clear();
                        usersTagged.clear();

                        for (int i = jsonArray.length() - 1; i >= jsonArray.length() - Math.min(jsonArray.length(), 3); i--) {
                            final User user = new User(jsonArray.getJSONObject(i));

                            usersTagged.add(user);
                            popup.getMenu().add(Menu.NONE, Menu.NONE, i + 1, "@" + user.getUsername());
                        }

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                String currentStr = getText().toString();
                                String strWithTag = currentStr.substring(0, tagStartsAt) + item.getTitle() + " " + currentStr.substring(tagStartsAt + tagWritting.length() + 1, currentStr.length());

                                String id = "";
                                for (User u : usersTagged) {
                                    String username = "@" + u.getUsername();
                                    if (username.equals(item.toString()))
                                        id = u.getId();
                                }

                                tags.add(new Tag("", id, item.toString()));

                                autochange = true;
                                setText(strWithTag);
                                setSelection(tagStartsAt + item.toString().length() + 1);

                                userWrittingTag = false;
                                tagWritting = "";
                                tagStartsAt = 0;

                                return true;
                            }
                        });
                    }
                } catch (JSONException e) {
                    userWrittingTag = false;
                    tagWritting = "";
                    tagStartsAt = 0;

                    popup.dismiss();

                    e.printStackTrace();
                }

                popup.show();
            }
        });

        request.execute(HttpGet.ROOTSEARCHUSERS + "?token=" + HttpGet.credentials.getSessionToken(), jsonObject.toString());
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
}
