package fr.insapp.insapp.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.activities.ProfileActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 11/12/2016.
 */

public class NotificationRecyclerViewAdapter extends BaseRecyclerViewAdapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    protected List<Notification> notifications;

    protected OnNotificationItemClickListener listener;

    public interface OnNotificationItemClickListener {
        void onNotificationItemClick(Notification notification);
    }

    public NotificationRecyclerViewAdapter(Context context) {
        this.context = context;
        this.notifications = new ArrayList<>();
    }

    public void setOnItemClickListener(OnNotificationItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Notification notification) {
        this.notifications.add(notification);
        this.notifyDataSetChanged();
    }

    @Override
    public NotificationRecyclerViewAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
        return new NotificationRecyclerViewAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationRecyclerViewAdapter.NotificationViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        holder.text.setText(notification.getMessage());
        holder.date.setText(String.format(context.getResources().getString(R.string.ago), Operation.displayedDate(notification.getDate())));

        if (notification.getType().equals("tag") || notification.getType().equals("post")) {
            Call<Post> call = ServiceGenerator.create().getPostFromId(notification.getId());
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                    if (response.isSuccessful()) {
                        final Post post = response.body();
                        notification.setPost(post);

                        Glide
                                .with(context)
                                .load(ServiceGenerator.CDN_URL + post.getImage())
                                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.thumbnail);
                    }
                    else {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                    Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        else if (notification.getType().equals("post") || notification.getType().equals("event")) {
            Call<Club> call = ServiceGenerator.create().getClubFromId(notification.getSender());
            call.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                    if (response.isSuccessful()) {
                        final Club club = response.body();
                        notification.setClub(club);

                        Glide
                                .with(context)
                                .load(ServiceGenerator.CDN_URL + club.getProfilPicture())
                                .into(holder.avatar_notification);

                        holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                    Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        else if (notification.getType().equals("tag")) {
            Call<User> call = ServiceGenerator.create().getUserFromId(notification.getSender());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        final User user = response.body();
                        notification.setUser(user);

                        // get the drawable of avatar

                        final int id = context.getResources().getIdentifier(Operation.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                        Glide.with(context).load(id).into(holder.avatar_notification);

                        holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", user));
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });

        }

        else if (notification.getType().equals("event")) {
            Call<Event> call = ServiceGenerator.create().getEventFromId(notification.getContent());
            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if (response.isSuccessful()) {
                        final Event event = response.body();
                        notification.setEvent(event);

                        Glide
                                .with(context)
                                .load(ServiceGenerator.CDN_URL + event.getImage())
                                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0))
                                .into(holder.thumbnail);
                    }
                    else {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                    Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public TextView date;
        public CircleImageView avatar_notification;
        public ImageView thumbnail;

        public NotificationViewHolder(View view) {
            super(view);

            this.text = (TextView) view.findViewById(R.id.notification_text);
            this.date = (TextView) view.findViewById(R.id.notification_date);
            this.avatar_notification = (CircleImageView) view.findViewById(R.id.avatar_notification);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail_notification);
        }

        public void bind(final Notification notification, final OnNotificationItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNotificationItemClick(notification);
                }
            });
        }
    }
}
