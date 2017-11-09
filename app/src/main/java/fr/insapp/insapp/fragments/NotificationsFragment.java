package fr.insapp.insapp.fragments;

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

import com.bumptech.glide.Glide;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.adapters.NotificationRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Notifications;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Utils;
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

        this.adapter = new NotificationRecyclerViewAdapter(getContext(), Glide.with(this));
        adapter.setOnItemClickListener(new NotificationRecyclerViewAdapter.OnNotificationItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                switch (notification.getType()) {
                    case "tag":
                    case "post":
                        Call<Post> call1 = ServiceGenerator.create().getPostFromId(notification.getContent());
                        call1.enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                if (response.isSuccessful()) {
                                    startActivity(new Intent(getContext(), PostActivity.class).putExtra("post", response.body()));
                                }
                                else {
                                    Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                            }
                        });

                        break;

                    case "eventTag":
                    case "event":
                        Call<Event> call2 = ServiceGenerator.create().getEventFromId(notification.getContent());
                        call2.enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                                if (response.isSuccessful()) {
                                    startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", response.body()));
                                }
                                else {
                                    Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                            }
                        });

                        break;

                    default:
                        break;
                }

                // mark notification as seen

                final User user = Utils.getUser();

                Call<Notifications> call = ServiceGenerator.create().markNotificationAsSeen(user.getId(), notification.getId());
                call.enqueue(new Callback<Notifications>() {
                    @Override
                    public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                        if (response.isSuccessful()) {

                        }
                        else {
                            Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
                        Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                    }
                });
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
        final User user = Utils.getUser();

        Call<Notifications> call = ServiceGenerator.create().getNotificationsForUser(user.getId());
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                if (response.isSuccessful()) {
                    final Notifications notifications = response.body();

                    if (notifications.getNotifications() != null) {
                        for (final Notification notification : notifications.getNotifications()) {
                            adapter.addItem(notification);
                        }
                    }
                }
                else {
                    Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
                Toast.makeText(App.getAppContext(), "NotificationsFragment", Toast.LENGTH_LONG).show();
            }
        });
    }
}