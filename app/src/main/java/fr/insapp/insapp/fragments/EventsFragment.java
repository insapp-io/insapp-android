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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.LoginActivity;
import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Event;

import static android.app.Activity.RESULT_OK;

/**
 * Created by thoma on 27/10/2016.
 */

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;
    private String filter_club_id;

    private View view;

    private RecyclerView recyclerViewNow;
    private RecyclerView recyclerViewToday;
    private RecyclerView recyclerViewWeek;
    private RecyclerView recyclerViewNextWeek;
    private RecyclerView recyclerViewLater;

    private EventRecyclerViewAdapter adapterNow;
    private EventRecyclerViewAdapter adapterToday;
    private EventRecyclerViewAdapter adapterWeek;
    private EventRecyclerViewAdapter adapterNextWeek;
    private EventRecyclerViewAdapter adapterLater;

    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int EVENT_REQUEST = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.row_event_with_avatars);
            this.filter_club_id = bundle.getString("filter_club_id");
        }

        // adapters

        this.adapterNow = new EventRecyclerViewAdapter(getContext(), layout);
        adapterNow.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });

        this.adapterToday = new EventRecyclerViewAdapter(getContext(), layout);
        adapterToday.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });

        this.adapterWeek = new EventRecyclerViewAdapter(getContext(), layout);
        adapterWeek.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });

        this.adapterNextWeek = new EventRecyclerViewAdapter(getContext(), layout);
        adapterNextWeek.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });

        this.adapterLater = new EventRecyclerViewAdapter(getContext(), layout);
        adapterLater.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_events, container, false);

        // recycler view

        this.recyclerViewNow = (RecyclerView) view.findViewById(R.id.recyclerview_events_now);
        recyclerViewNow.setHasFixedSize(true);
        recyclerViewNow.setNestedScrollingEnabled(false);

        this.recyclerViewToday = (RecyclerView) view.findViewById(R.id.recyclerview_events_today);
        recyclerViewToday.setHasFixedSize(true);
        recyclerViewToday.setNestedScrollingEnabled(false);

        this.recyclerViewWeek = (RecyclerView) view.findViewById(R.id.recyclerview_events_week);
        recyclerViewWeek.setHasFixedSize(true);
        recyclerViewWeek.setNestedScrollingEnabled(false);

        this.recyclerViewNextWeek = (RecyclerView) view.findViewById(R.id.recyclerview_events_next_week);
        recyclerViewNextWeek.setHasFixedSize(true);
        recyclerViewNextWeek.setNestedScrollingEnabled(false);

        this.recyclerViewLater = (RecyclerView) view.findViewById(R.id.recyclerview_events_later);
        recyclerViewLater.setHasFixedSize(true);
        recyclerViewLater.setNestedScrollingEnabled(false);

        recyclerViewNow.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNow.setAdapter(adapterNow);

        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewToday.setAdapter(adapterToday);

        recyclerViewWeek.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewWeek.setAdapter(adapterWeek);

        recyclerViewNextWeek.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNextWeek.setAdapter(adapterNextWeek);

        recyclerViewLater.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewLater.setAdapter(adapterLater);

        // swipe refresh layout

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_events);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        generateEvents();

        return view;
    }

    private void clearEvents() {
        adapterNow.getEvents().clear();
        adapterToday.getEvents().clear();
        adapterWeek.getEvents().clear();
        adapterNextWeek.getEvents().clear();
        adapterLater.getEvents().clear();

        view.findViewById(R.id.events_now_layout).setVisibility(View.GONE);
        view.findViewById(R.id.events_today_layout).setVisibility(View.GONE);
        view.findViewById(R.id.events_week_layout).setVisibility(View.GONE);
        view.findViewById(R.id.events_next_week_layout).setVisibility(View.GONE);
        view.findViewById(R.id.events_later_layout).setVisibility(View.GONE);
    }

    private void generateEvents() {
        clearEvents();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.isEmpty()) {
                    startActivityForResult(new Intent(getContext(), LoginActivity.class), MainActivity.REFRESH_TOKEN_MESSAGE);
                }
                else if (!output.equals("{\"events\":null}")) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject = jsonarray.getJSONObject(i);

                            Event event = new Event(jsonObject);
                            Date atm = Calendar.getInstance().getTime();

                            if (event.getDateEnd().getTime() > atm.getTime()) {
                                if (filter_club_id != null) {
                                    if (filter_club_id.equals(event.getAssociation()))
                                        addEventToAdapter(event);
                                }
                                else
                                    addEventToAdapter(event);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        /*
        request.execute(HttpGet.ROOTEVENT + "?token=" + HttpGet.sessionCredentials.getSessionToken());
        */
    }

    private void addEventToAdapter(Event event) {
        Calendar now = Calendar.getInstance();

        if (event.getDateStart().getTime() <= now.getTime().getTime() && event.getDateEnd().getTime() > now.getTime().getTime()) {
            adapterNow.addItem(event);
            view.findViewById(R.id.events_now_layout).setVisibility(View.VISIBLE);
            return;
        }

        Calendar tomorrow = Calendar.getInstance();

        // tomorrow midnight

        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);


        if (event.getDateStart().getTime() <= tomorrow.getTime().getTime()) {
            adapterToday.addItem(event);
            view.findViewById(R.id.events_today_layout).setVisibility(View.VISIBLE);
            return;
        }

        // saturday at midday

        Calendar week = Calendar.getInstance();

        while (week.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
            week.add(Calendar.DATE, 1);

        week.set(Calendar.HOUR_OF_DAY, 12);
        week.set(Calendar.MINUTE, 0);
        week.set(Calendar.SECOND, 0);
        week.set(Calendar.MILLISECOND, 0);

        if (event.getDateStart().getTime() <= week.getTime().getTime()) {
            adapterWeek.addItem(event);
            view.findViewById(R.id.events_week_layout).setVisibility(View.VISIBLE);
            return;
        }

        // saturday (next week) at midday

        Calendar nextWeek = Calendar.getInstance();

        while (nextWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
            nextWeek.add(Calendar.DATE, 1);

        nextWeek.set(Calendar.HOUR_OF_DAY, 12);
        nextWeek.set(Calendar.MINUTE, 0);
        nextWeek.set(Calendar.SECOND, 0);
        nextWeek.set(Calendar.MILLISECOND, 0);
        nextWeek.add(Calendar.WEEK_OF_MONTH, 1);

        if (event.getDateStart().getTime() <= nextWeek.getTime().getTime()) {
            adapterNextWeek.addItem(event);
            view.findViewById(R.id.events_next_week_layout).setVisibility(View.VISIBLE);
            return;
        }

        adapterLater.addItem(event);
        view.findViewById(R.id.events_later_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case EVENT_REQUEST:

                    Event event = data.getParcelableExtra("event");
                    final int idToday = adapterToday.getEvents().indexOf(event);
                    final int idWeek = adapterWeek.getEvents().indexOf(event);
                    final int idMonth = adapterLater.getEvents().indexOf(event);

                    if (idToday >= 0)
                        adapterToday.updatePost(idToday, event);
                    else if (idWeek >= 0)
                        adapterWeek.updatePost(idWeek, event);
                    else if (idMonth >= 0)
                        adapterLater.updatePost(idMonth, event);
                    break;

                case MainActivity.REFRESH_TOKEN_MESSAGE:
                    generateEvents();
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        generateEvents();
    }
}