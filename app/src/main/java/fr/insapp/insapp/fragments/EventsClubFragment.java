package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Event;

/**
 * Created by thoma on 09/12/2016.
 */

public class EventsClubFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;

    private View view;
    private EventRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.row_event);
        }

        // adapter

        this.adapter = new EventRecyclerViewAdapter(getContext(), generateEvents(), layout);
        adapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                getContext().startActivity(new Intent(getContext(), EventActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_events_club, container, false);

        RecyclerView recyclerViewFuture = (RecyclerView) view.findViewById(R.id.recyclerview_events_future);
        recyclerViewFuture.setHasFixedSize(true);
        recyclerViewFuture.setNestedScrollingEnabled(false);

        RecyclerView recyclerViewPast = (RecyclerView) view.findViewById(R.id.recyclerview_events_past);
        recyclerViewPast.setHasFixedSize(true);
        recyclerViewPast.setNestedScrollingEnabled(false);

        recyclerViewFuture.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFuture.setAdapter(adapter);

        recyclerViewPast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPast.setAdapter(adapter);

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_events_club);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private List<Event> generateEvents() {
        List<Event> events = new ArrayList<>();

        //for (int i = 0; i < 1; i++)
        //    events.add(new Event(R.drawable.sample_0, "Un événement trop cool"));

        return events;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}
