package fr.insapp.insapp.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import fr.insapp.insapp.utility.CommentEditText;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 25/02/2017.
 */

public class CommentsEventFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;

    private Event event;

    // comment

    private CircleImageView circleImageView;
    private CommentEditText commentEditText;
    private PopupMenu popup;

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

        // popup menu

        this.popup = new PopupMenu(getContext(), commentEditText);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                menu.show();
            }
        });

        // edit text

        this.commentEditText = (CommentEditText) view.findViewById(R.id.comment_event_input);
        commentEditText.setTextChangedListener(popup);

        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        commentEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                                    event.refresh(new JSONObject(output));
                                    adapter.setComments(event.getComments());

                                    commentEditText.getText().clear();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/comment?token=" + HttpGet.credentials.getSessionToken(), json.toString());
                    }

                    return true;
                }

                return false;
            }
        });

        // recycler view

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comments_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        return view;
    }
}
