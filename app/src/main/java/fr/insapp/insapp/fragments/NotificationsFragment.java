package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.LoginActivity;
import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.NotificationRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Notification;

import static android.app.Activity.RESULT_OK;

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

        this.adapter = new NotificationRecyclerViewAdapter(getContext());
        adapter.setOnItemClickListener(new NotificationRecyclerViewAdapter.OnNotificationItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                if (notification.getType().equals("tag"))
                    startActivity(new Intent(getContext(), PostActivity.class).putExtra("post", notification.getPost()).putExtra("taggedCommentID", notification.getCommentID()));
                else if (notification.getType().equals("post"))
                    startActivity(new Intent(getContext(), PostActivity.class).putExtra("post", notification.getPost()));
                else if (notification.getType().equals("event"))
                    startActivity(new Intent(getContext(), EventActivity.class).putExtra("event", notification.getEvent()));
            }
        });

        generateNotifications();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REFRESH_TOKEN_MESSAGE:
                    generateNotifications();
                    break;
            }
        }
    }

    private void generateNotifications() {
        HttpGet request = new HttpGet(new AsyncResponse() {
            public void processFinish(String output) {
                if (output.isEmpty()) {
                    startActivityForResult(new Intent(getContext(), LoginActivity.class), MainActivity.REFRESH_TOKEN_MESSAGE);
                }
                else if (!output.equals("{\"notifications\":null}")) {
                    try {
                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("notifications");

                        if (jsonarray != null) {
                            for (int i = 0; i < jsonarray.length(); i++) {
                                final JSONObject jsonobject = jsonarray.getJSONObject(i);

                                adapter.addItem(new Notification(jsonobject));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*
        request.execute(HttpGet.ROOTNOTIFICATION + "/" + HttpGet.sessionCredentials.getUserID() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
        */
    }
}