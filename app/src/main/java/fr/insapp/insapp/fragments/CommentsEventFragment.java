package fr.insapp.insapp.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

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

        HttpPost request = new HttpPost(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    event.refresh(new JSONObject(output));
                    adapter.setComments(event.getComments());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
        String params = HttpGet.ROOTEVENT + "/" + event.getId() + "/comment?token=" + HttpGet.credentials.getSessionToken();

        this.commentEditText = (CommentEditText) view.findViewById(R.id.comment_event_input);
        //commentEditText.setupComponent(request, params);

        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EventActivity) getActivity()).getAppBarLayout().setExpanded(false, true);
                    ((EventActivity) getActivity()).getFloatingActionMenu().hideMenu(false);
                }
                else {
                    final Date atm = Calendar.getInstance().getTime();
                    if (event.getDateEnd().getTime() >= atm.getTime()) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((EventActivity) getActivity()).getFloatingActionMenu().showMenu(true);
                            }
                        }, 500);
                    }
                }
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
