package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 19/07/2017.
 */

public class EventInteraction {

    @SerializedName("event")
    private Event event;

    @SerializedName("user")
    private User user;

    public EventInteraction(Event event, User user) {
        this.event = event;
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }
}
