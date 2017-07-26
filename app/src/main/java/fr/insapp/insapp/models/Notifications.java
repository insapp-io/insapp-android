package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 26/07/2017.
 */

public class Notifications {

    @SerializedName("notifications")
    private List<Notification> notifications;

    public Notifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
