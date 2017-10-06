package fr.insapp.insapp.models;

import java.util.Comparator;

/**
 * Created by thomas on 01/03/2017.
 */

public class EventComparator implements Comparator<Event> {

    private boolean past = false;

    public EventComparator(boolean past) {
        this.past = past;
    }

    @Override
    public int compare(Event event1, Event event2) {
        if (event1.getDateStart().getTime() == event2.getDateStart().getTime())
            return 0;
        if (event1.getDateStart().getTime() < event2.getDateStart().getTime())
            return past ? 1 : -1;

        return past ? -1 : 1;
    }
}
