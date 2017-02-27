package fr.insapp.insapp.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.listeners.EventCommentLongClickListener;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 25/02/2017.
 */

public class CommentsEventFragment extends Fragment {

    private View view;

    private CircleImageView circleImageView;
    private EditText editText;
    private PopupMenu popup;

    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;

    private Event event;

    private ArrayList<Tag> tags = new ArrayList<>();

    private boolean userWrittingTag = false;
    private int tagStartsAt = 0;
    private String tagWritting = "";
    private boolean autochange = false;
    private boolean deleteTag = false;
    private int lastCount = 0;

    private List<User> usersTagged = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.event = bundle.getParcelable("event");
        }

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(getContext(), event.getComments());
        adapter.setOnItemLongClickListener(new EventCommentLongClickListener(getContext(), event, adapter));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_comments_event, container, false);

        // comment user avatar

        this.circleImageView = (CircleImageView) view.findViewById(R.id.comment_event_username_avatar);

        // get the drawable of avatar
        Resources resources = getContext().getResources();
        final int id = resources.getIdentifier(Operation.drawableProfilName(MainActivity.getUser().getPromotion(), MainActivity.getUser().getGender()), "drawable", getContext().getPackageName());
        Glide.with(getContext()).load(id).into(circleImageView);

        // edit text

        this.editText = (EditText) view.findViewById(R.id.comment_event_input);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EventActivity) getActivity()).getAppBarLayout().setExpanded(false, true);
                    ((EventActivity) getActivity()).getFloatingActionMenu().hideMenu(false);
                }
                else {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((EventActivity) getActivity()).getFloatingActionMenu().showMenu(true);
                        }
                    }, 500);
                }
            }
        });

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
                        //popup.dismiss();
                    }
                }
                // writing
                else {
                    deleteTag = false;

                    int pos = editText.getSelectionStart() - 1;
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

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    System.out.println("test");

                    return true;
                }

                return false;
            }
        });

        // popup menu

        this.popup = new PopupMenu(getActivity(), editText);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

        // recycler view

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comments_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        return view;
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
                                String currentStr = editText.getText().toString();
                                String strWithTag = currentStr.substring(0, tagStartsAt) + item.getTitle() + " " + currentStr.substring(tagStartsAt + tagWritting.length() + 1, currentStr.length());

                                String id = "";
                                for (User u : usersTagged) {
                                    String username = "@" + u.getUsername();
                                    if (username.equals(item.toString()))
                                        id = u.getId();
                                }

                                tags.add(new Tag("", id, item.toString()));

                                autochange = true;
                                editText.setText(strWithTag);
                                editText.setSelection(tagStartsAt + item.toString().length() + 1);

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
}
