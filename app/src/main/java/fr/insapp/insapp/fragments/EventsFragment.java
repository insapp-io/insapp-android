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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.R;

/**
 * Created by thoma on 27/10/2016.
 */

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

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
            this.layout = bundle.getInt("layout", R.layout.row_event_with_avatars);
        }

        // adapter

        this.adapter = new EventRecyclerViewAdapter(getContext(), layout);
        adapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                getContext().startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", event));
            }
        });

        generateEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_events, container, false);

        RecyclerView recyclerViewToday = (RecyclerView) view.findViewById(R.id.recyclerview_events_today);
        recyclerViewToday.setHasFixedSize(true);
        recyclerViewToday.setNestedScrollingEnabled(false);

        RecyclerView recyclerViewWeek = (RecyclerView) view.findViewById(R.id.recyclerview_events_week);
        recyclerViewWeek.setHasFixedSize(true);
        recyclerViewWeek.setNestedScrollingEnabled(false);

        RecyclerView recyclerViewMonth = (RecyclerView) view.findViewById(R.id.recyclerview_events_month);
        recyclerViewMonth.setHasFixedSize(true);
        recyclerViewMonth.setNestedScrollingEnabled(false);

        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewToday.setAdapter(adapter);

        recyclerViewWeek.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewWeek.setAdapter(adapter);

        recyclerViewMonth.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewMonth.setAdapter(adapter);

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_events);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void generateEvents() {
        adapter.getEvents().clear();
        adapter.notifyDataSetChanged();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output != null) {

                    Date atm = Calendar.getInstance().getTime();

                    try {
                        JSONArray jsonarray = new JSONArray(output);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject = jsonarray.getJSONObject(i);

                            Event event = new Event(jsonObject);
                            if (event.getDateEnd().getTime() > atm.getTime())
                                adapter.addItem(event);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTEVENT + "?token=" + HttpGet.credentials.getSessionToken());

        Date d = new Date();
        d.setTime((long)(d.getTime()*1.2));
        Event event = new Event("id", "Test Event Insapp", "5803b463923c84b95c000001", "Le Lorem Ipsum est simplement du faux texte employé dans la composition et la mise en page avant impression. Le Lorem Ipsum est le faux texte standard de l'imprimerie depuis les années 1500, quand un peintre anonyme assembla ensemble des morceaux de texte pour réaliser un livre spécimen de polices de texte. Il n'a pas fait que survivre cinq siècles, mais s'est aussi adapté à la bureautique informatique, sans que son contenu n'en soit modifié. Il a été popularisé dans les années 1960 grâce à la vente de feuilles Letraset contenant des passages du Lorem Ipsum, et, plus récemment, par son inclusion dans des applications de mise en page de texte, comme Aldus PageMaker.", new ArrayList<String>(), "", new Date(), d, "8t58m00hwwkvvflon8p1fo0imwt5awhcacnyypag.png", "161215", "ffffff");
        adapter.addItem(event);
    }

    @Override
    public void onRefresh() {
        generateEvents();
        swipeRefreshLayout.setRefreshing(false);
    }
}