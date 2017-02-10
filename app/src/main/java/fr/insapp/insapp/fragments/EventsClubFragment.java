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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;

/**
 * Created by thoma on 09/12/2016.
 */

public class EventsClubFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;
    private int swipeColor;

    private Club club;

    private View view;
    private EventRecyclerViewAdapter adapterFuture;
    private EventRecyclerViewAdapter adapterPast;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.row_event);
            this.club = bundle.getParcelable("club");
            this.swipeColor = bundle.getInt("swipe_color");
        }

        // adapter

        this.adapterFuture = new EventRecyclerViewAdapter(getContext(), layout);
        adapterFuture.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", event));
            }
        });

        this.adapterPast = new EventRecyclerViewAdapter(getContext(), layout);
        adapterPast.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", event));
            }
        });

        generateEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_events_club, container, false);

        // recycler view

        RecyclerView recyclerViewFuture = (RecyclerView) view.findViewById(R.id.recyclerview_events_future);
        recyclerViewFuture.setHasFixedSize(true);
        recyclerViewFuture.setNestedScrollingEnabled(false);

        RecyclerView recyclerViewPast = (RecyclerView) view.findViewById(R.id.recyclerview_events_past);
        recyclerViewPast.setHasFixedSize(true);
        recyclerViewPast.setNestedScrollingEnabled(false);

        recyclerViewFuture.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFuture.setAdapter(adapterFuture);

        recyclerViewPast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPast.setAdapter(adapterPast);

        // swipe refresh layout

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_events_club);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(swipeColor);

        return view;
    }

    private void generateEvents() {
        adapterFuture.getEvents().clear();
        adapterPast.getEvents().clear();

        /*
        view.findViewById(R.id.events_future_layout).setVisibility(LinearLayout.GONE);
        view.findViewById(R.id.events_past_layout).setVisibility(LinearLayout.GONE);
        */

        for (int j = 0; j < club.getEvents().size(); j++) {
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    swipeRefreshLayout.setRefreshing(false);

                    try {
                        JSONObject jsonObject = new JSONObject(output);

                        Event event = new Event(jsonObject);
                        Date atm = Calendar.getInstance().getTime();

                        if (event.getDateEnd().getTime() > atm.getTime()) {
                            adapterFuture.addItem(event);
                            adapterFuture.notifyDataSetChanged();

                            /*
                            if (view.findViewById(R.id.events_future_layout).getVisibility() != LinearLayout.VISIBLE)
                                view.findViewById(R.id.events_future_layout).setVisibility(LinearLayout.VISIBLE);
                            */
                        } else {
                            adapterPast.addItem(event);
                            adapterPast.notifyDataSetChanged();

                            /*
                            if (view.findViewById(R.id.events_past_layout).getVisibility() != LinearLayout.VISIBLE)
                                view.findViewById(R.id.events_past_layout).setVisibility(LinearLayout.VISIBLE);
                            */
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTEVENT + "/" + club.getEvents().get(j) + "?token=" + HttpGet.credentials.getSessionToken());
        }
    }

    @Override
    public void onRefresh() {
        generateEvents();
    }
}
