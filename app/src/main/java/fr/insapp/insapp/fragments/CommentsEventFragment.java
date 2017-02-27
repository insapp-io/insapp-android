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
import android.widget.EditText;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.listeners.EventCommentLongClickListener;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 25/02/2017.
 */

public class CommentsEventFragment extends Fragment {

    private View view;

    private CircleImageView circleImageView;
    private EditText editText;

    private RecyclerView recyclerView;
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
