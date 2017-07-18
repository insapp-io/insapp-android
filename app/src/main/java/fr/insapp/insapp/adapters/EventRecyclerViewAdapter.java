package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.EventComparator;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 18/11/2016.
 */

public class EventRecyclerViewAdapter extends BaseRecyclerViewAdapter<EventRecyclerViewAdapter.EventViewHolder> {

    protected List<Event> events;

    private int layout;

    private OnEventItemClickListener listener;

    public interface OnEventItemClickListener {
        void onEventItemClick(Event event);
    }

    public EventRecyclerViewAdapter(Context context, int layout) {
        this.context = context;
        this.events = new ArrayList<>();
        this.layout = layout;
    }

    public void setOnItemClickListener(OnEventItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Event event) {
        this.events.add(event);
        Collections.sort(events, new EventComparator());

        this.notifyDataSetChanged();
    }

    public void updatePost(int id, Event event){
        this.events.set(id, event);
        this.notifyItemChanged(id);
    }

    @Override
    public EventRecyclerViewAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new EventRecyclerViewAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventRecyclerViewAdapter.EventViewHolder holder, int position) {
        final Event event = events.get(position);

        if (layout == R.layout.row_event_with_avatars) {
            Call<Club> call = ServiceGenerator.create().getClubFromId(event.getAssociation());
            call.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                    if (response.isSuccessful()) {
                        final Club club = response.body();

                        Glide
                                .with(context)
                                .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
                                .crossFade()
                                .into(holder.avatar);

                        holder.avatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, "EventRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                    Toast.makeText(context, "EventRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        Glide.with(context)
                .load(ServiceGenerator.CDN_URL + event.getImage())
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

        holder.name.setText(event.getName());

        final int nb_participants = (event.getAttendees() == null) ? 0 : event.getAttendees().size();
        if (nb_participants <= 1) {
            holder.participants.setText(Integer.toString(nb_participants) + " participant");
        }
        else {
            holder.participants.setText(Integer.toString(nb_participants) + " participants");
        }

        final int diffInDays = (int) ((event.getDateEnd().getTime() - event.getDateStart().getTime()) / (1000 * 60 * 60 * 24));
        if (diffInDays < 1 && event.getDateStart().getMonth() == event.getDateEnd().getMonth()) {
            DateFormat dateFormat_oneday = new SimpleDateFormat("'Le' dd/MM 'à' HH:mm");

            holder.date.setText(dateFormat_oneday.format(event.getDateStart()));
        }
        else {
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

    public List<Event> getEvents() {
        return events;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;
        public CircleImageView avatar;
        public TextView date;
        public TextView participants;

        public EventViewHolder(View view) {
            super(view);

            this.avatar = (CircleImageView) view.findViewById(R.id.avatar_club_event);

            this.name = (TextView) view.findViewById(R.id.name_event);
            this.date = (TextView) view.findViewById(R.id.date_event);
            this.participants = (TextView) view.findViewById(R.id.going_event);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail_event);
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