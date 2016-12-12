package fr.insapp.insapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.utility.ImageLoader;

/**
 * Created by thoma on 18/11/2016.
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    protected List<Event> events;

    private int layout;

    private ImageLoader imageLoader;

    protected OnEventItemClickListener listener;

    public interface OnEventItemClickListener {
        void onEventItemClick(Event event);
    }

    public EventRecyclerViewAdapter(Context context, List<Event> events, int layout) {
        this.events = events;
        this.layout = layout;
        this.imageLoader = new ImageLoader(context);
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

        imageLoader.DisplayImage(HttpGet.IMAGEURL + event.getImage(), holder.image);
        holder.name.setText(event.getName());

        int nb_participants = event.getParticipants().size();
        if (nb_participants <= 1)
            holder.participants.setText(Integer.toString(nb_participants) + " participant");
        else
            holder.participants.setText(Integer.toString(nb_participants) + " participants");

        if (event.getDateStart().getDay() == event.getDateEnd().getDay() && event.getDateStart().getMonth() == event.getDateEnd().getMonth()) {
            DateFormat dateFormat_oneday = new SimpleDateFormat("'Le' dd/MM 'à' HH:mm");

            holder.date.setText(dateFormat_oneday.format(event.getDateStart()));
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM");
            String dateStart = dateFormat.format(event.getDateStart());
            String dateEnd = dateFormat.format(event.getDateEnd());

            holder.date.setText("Du " + dateStart + " au " + dateEnd);
        }

        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        //public ImageView thumbnail;
        public TextView name;
        public CircleImageView image;
        public TextView date;
        public TextView participants;

        public EventViewHolder(View view) {
            super(view);

            //this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail_event);
            this.name = (TextView) view.findViewById(R.id.name_event);
            this.image = (CircleImageView) view.findViewById(R.id.avatar_club_event);
            this.date = (TextView) view.findViewById(R.id.date_event);
            this.participants = (TextView) view.findViewById(R.id.going_event);
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