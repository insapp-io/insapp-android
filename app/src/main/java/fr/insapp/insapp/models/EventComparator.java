package fr.insapp.insapp.models;

import java.util.Comparator;

import fr.insapp.insapp.models.Event;

/**
 * Created by thomas on 01/03/2017.
 */

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event event1, Event event2) {
        if (event1.getDateStart().getTime() == event2.getDateStart().getTime())
            return 0;
        if (event1.getDateStart().getTime() < event2.getDateStart().getTime())
            return -1;

        return 1;
    }
}
