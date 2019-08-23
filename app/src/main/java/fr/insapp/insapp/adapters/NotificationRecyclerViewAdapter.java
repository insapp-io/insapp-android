package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.activities.ProfileActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thomas on 11/12/2016.
 */

public class NotificationRecyclerViewAdapter extends BaseRecyclerViewAdapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    private RequestManager requestManager;

    protected List<Notification> notifications;

    protected OnNotificationItemClickListener listener;

    public interface OnNotificationItemClickListener {
        void onNotificationItemClick(Notification notification);
    }

    public NotificationRecyclerViewAdapter(Context context, RequestManager requestManager) {
        this.context = context;
        this.requestManager = requestManager;
        this.notifications = new ArrayList<>();
    }

    public void setOnItemClickListener(OnNotificationItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Notification> notifications) {
        this.notifications = notifications;
        this.notifyDataSetChanged();
    }

    public void addItem(Notification notification) {
        this.notifications.add(notification);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationRecyclerViewAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
        return new NotificationRecyclerViewAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationRecyclerViewAdapter.NotificationViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        holder.text.setText(notification.getMessage());
        holder.date.setText(Utils.INSTANCE.displayedDate(notification.getDate()));

        // avatars

        if (notification.getType().equals("tag") || notification.getType().equals("eventTag")) {
            Call<User> call = ServiceGenerator.create().getUserFromId(notification.getSender());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        final User user = response.body();

                        if (user != null) {
                            final int id = context.getResources().getIdentifier(Utils.INSTANCE.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                            requestManager
                                .load(id)
                                .apply(RequestOptions.circleCropTransform())
                                .transition(withCrossFade())
                                .into(holder.avatar_notification);

                            holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", user));
                                }
                            });
                        }
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

        else if (notification.getType().equals("post") || notification.getType().equals("event")) {
            Call<Club> call = ServiceGenerator.create().getClubFromId(notification.getSender());
            call.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                    if (response.isSuccessful()) {
                        final Club club = response.body();

                        if (club != null) {
                            requestManager
                                .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
                                .apply(RequestOptions.circleCropTransform())
                                .transition(withCrossFade())
                                .into(holder.avatar_notification);

                            holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                                }
                            });
                        }
                    } else {
                        Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                    Toast.makeText(context, "NotificationRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        // thumbnails

        if (notification.getType().equals("tag") || notification.getType().equals("post")) {
            Call<Post> call = ServiceGenerator.create().getPostFromId(notification.getContent());
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                    if (response.isSuccessful()) {
                        final Post post = response.body();

                        if (post != null) {
                            requestManager
                                .load(ServiceGenerator.CDN_URL + post.getImage())
                                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8)))
                                .transition(withCrossFade())
                                .into(holder.thumbnail);
                        }
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

        else if (notification.getType().equals("eventTag") || notification.getType().equals("event")) {
            Call<Event> call = ServiceGenerator.create().getEventFromId(notification.getContent());
            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if (response.isSuccessful()) {
                        final Event event = response.body();

                        if (event != null) {
                            requestManager
                                .load(ServiceGenerator.CDN_URL + event.getImage())
                                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8)))
                                .transition(withCrossFade())
                                .into(holder.thumbnail);
                        }
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
        public ImageView avatar_notification;
        public ImageView thumbnail;

        public NotificationViewHolder(View view) {
            super(view);

            this.text = view.findViewById(R.id.notification_text);
            this.date = view.findViewById(R.id.notification_date);
            this.avatar_notification = view.findViewById(R.id.avatar_notification);
            this.thumbnail = view.findViewById(R.id.thumbnail_notification);
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
