package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.NotificationRecyclerViewAdapter;
import fr.insapp.insapp.R;
import fr.insapp.insapp.modeles.Notification;

/**
 * Created by thoma on 27/10/2016.
 */

public class NotificationsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private NotificationRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adapter

        this.adapter = new NotificationRecyclerViewAdapter(generateNotifications());
        adapter.setOnItemClickListener(new NotificationRecyclerViewAdapter.OnNotificationItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_notifications, container, false);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_notifications);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Notification> generateNotifications() {
        List<Notification> notifications = new ArrayList<>();

        for (int i = 0; i < 22; i++)
            notifications.add(new Notification("Bebop t'invite à Concert rap", "il y a 3j"));

        return notifications;
    }
}