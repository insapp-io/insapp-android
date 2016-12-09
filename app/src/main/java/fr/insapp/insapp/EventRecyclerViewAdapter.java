package fr.insapp.insapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.insapp.insapp.modeles.Event;

/**
 * Created by thoma on 18/11/2016.
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    protected List<Event> events;

    private int layout;

    protected OnEventItemClickListener listener;

    public interface OnEventItemClickListener {
        void onEventItemClick(Event event);
    }

    public EventRecyclerViewAdapter(List<Event> events, int layout) {
        this.events = events;
        this.layout = layout;
    }

    public void setOnItemClickListener(OnEventItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public EventRecyclerViewAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new EventRecyclerViewAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventRecyclerViewAdapter.EventViewHolder holder, int position) {
        final Event event = events.get(position);
        //holder.thumbnail.setImageResource(event.thumbnail_id);
        holder.name.setText(event.name);

        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        //public ImageView thumbnail;
        public TextView name;

        public EventViewHolder(View view) {
            super(view);
            //this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail_event);
            this.name = (TextView) view.findViewById(R.id.name_event);
        }

        public void bind(final Event event, final OnEventItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEventItemClick(event);
                }
            });
        }
    }
}