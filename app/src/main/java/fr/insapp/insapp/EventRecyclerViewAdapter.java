package fr.insapp.insapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.insapp.insapp.modeles.Event;

/**
 * Created by thoma on 18/11/2016.
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    protected List<Event> events;

    public EventRecyclerViewAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public EventRecyclerViewAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false);
        EventRecyclerViewAdapter.EventViewHolder holder = new EventRecyclerViewAdapter.EventViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventRecyclerViewAdapter.EventViewHolder holder, int position) {
        final Event event = events.get(position);
        holder.thumbnail.setImageResource(event.thumbnail_id);
        holder.name.setText(event.name);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;

        public EventViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail_event);
            this.name = (TextView) view.findViewById(R.id.name_event);
        }
    }
}