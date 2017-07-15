package fr.insapp.insapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomas on 15/07/2017.
 */

public class EventSearchResults {

    @SerializedName("events")
    private List<Event> events;

    public EventSearchResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
