package fr.insapp.insapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.listeners.EventCommentLongClickListener;
import fr.insapp.insapp.models.Event;

/**
 * Created by thoma on 25/02/2017.
 */

public class CommentsEventFragment extends Fragment {

    private View view;

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
