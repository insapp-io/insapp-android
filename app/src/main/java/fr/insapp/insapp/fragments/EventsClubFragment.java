package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by thomas on 09/12/2016.
 */

public class EventsClubFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;
    private int swipeColor;

    private Club club;

    private View view;
    private EventRecyclerViewAdapter adapterFuture;
    private EventRecyclerViewAdapter adapterPast;

    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int EVENT_REQUEST = 2;

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

        // adapters

        final RequestManager requestManager = Glide.with(this);

        this.adapterFuture = new EventRecyclerViewAdapter(getContext(), requestManager, false, layout);
        adapterFuture.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });

        this.adapterPast = new EventRecyclerViewAdapter(getContext(), requestManager, true, layout);
        adapterPast.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivityForResult(new Intent(getContext(), EventActivity.class).putExtra("event", event), EVENT_REQUEST);
            }
        });
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

        generateEvents();

        return view;
    }

    private void clearEvents() {
        adapterFuture.getEvents().clear();
        adapterPast.getEvents().clear();

        view.findViewById(R.id.events_future_layout).setVisibility(View.GONE);
        view.findViewById(R.id.events_past_layout).setVisibility(View.GONE);
    }

    private void generateEvents() {
        clearEvents();

        for (int j = 0; j < club.getEvents().size(); j++) {
            Call<Event> call = ServiceGenerator.create().getEventFromId(club.getEvents().get(j));
            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if (response.isSuccessful()) {
                        addEventToAdapter(response.body());
                    }
                    else {
                        Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show();
                    }

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                    Toast.makeText(App.getAppContext(), "EventsClubFragment", Toast.LENGTH_LONG).show();

                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    private void addEventToAdapter(Event event) {
        final Date atm = Calendar.getInstance().getTime();

        if (event.getDateEnd().getTime() > atm.getTime()) {
            adapterFuture.addItem(event);
            view.findViewById(R.id.events_future_layout).setVisibility(View.VISIBLE);
        }
        else {
            adapterPast.addItem(event);
            view.findViewById(R.id.events_past_layout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == EVENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    final Event event = intent.getParcelableExtra("event");

                    for (int i = 0; i < adapterPast.getItemCount(); ++i) {
                        if (adapterPast.getEvents().get(i).getId().equals(event.getId())) {
                            adapterPast.updateEvent(i, event);
                        }
                    }

                    for (int i = 0; i < adapterFuture.getItemCount(); ++i) {
                        if (adapterFuture.getEvents().get(i).getId().equals(event.getId())) {
                            adapterFuture.updateEvent(i, event);
                        }
                    }

                    break;

                case RESULT_CANCELED:
                default:
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        generateEvents();
    }
}
