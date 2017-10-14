package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.listeners.EventCommentLongClickListener;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.CommentEditText;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thomas on 25/02/2017.
 */

public class CommentsEventFragment extends Fragment {

    private CommentRecyclerViewAdapter adapter;

    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.event = bundle.getParcelable("event");
        }

        // adapter

        this.adapter = new CommentRecyclerViewAdapter(getContext(), Glide.with(this), event.getComments());
        adapter.setOnItemLongClickListener(new EventCommentLongClickListener(getContext(), event, adapter));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_comments, container, false);

        CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.comment_event_username_avatar);

        // edit comment

        CommentEditText commentEditText = (CommentEditText) view.findViewById(R.id.comment_event_input);
        commentEditText.setupComponent(adapter, event);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comments_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        // retrieve the avatar of the user

        final User user = Utils.getUser();

        final int id = getResources().getIdentifier(Utils.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", getContext().getPackageName());
        Glide
                .with(getContext())
                .load(id)
                .crossFade()
                .into(circleImageView);

        // animation on focus

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

        return view;
    }
}
