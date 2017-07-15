package fr.insapp.insapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.adapters.NotificationRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 27/10/2016.
 */

public class NotificationsFragment extends Fragment {

    private NotificationRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adapter

        this.adapter = new NotificationRecyclerViewAdapter(getContext());
        adapter.setOnItemClickListener(new NotificationRecyclerViewAdapter.OnNotificationItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                if (notification.getType().equals("tag")) {
                    startActivity(new Intent(getContext(), PostActivity.class).putExtra("post", notification.getPost()).putExtra("taggedCommentID", notification.getComment().getId()));
                }
                else if (notification.getType().equals("post")) {
                    startActivity(new Intent(getContext(), PostActivity.class).putExtra("post", notification.getPost()));
                }
                else if (notification.getType().equals("event")) {
                    startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", notification.getEvent()));
                }
            }
        });

        generateNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_notifications);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void generateNotifications() {
        Call<List<Notification>> call = ServiceGenerator.create().getNotificationsForUser(new Gson().fromJson(getContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE).getString("session", ""), SessionCredentials.class).getUser().getId());
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    for (final Notification notification : response.body()) {
                        adapter.addItem(notification);
                    }
                }
                else {
                    Toast.makeText(getContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
            }
        });
    }
}