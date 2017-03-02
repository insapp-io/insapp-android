package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.ClubActivity;
import fr.insapp.insapp.ProfileActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by thoma on 11/12/2016.
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
            HttpGet post = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final Post post = new Post(new JSONObject(output));
                        notification.setPost(post);

                        Glide.with(context).load(HttpGet.IMAGEURL + post.getImage()).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.thumbnail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            post.execute(HttpGet.ROOTPOST + "/" + notification.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
        }

        if (notification.getType().equals("post") || notification.getType().equals("event")) {
            final Club club = HttpGet.clubs.get(notification.getSender());

            if (club == null){
                HttpGet request = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            final Club club = new Club(new JSONObject(output));
                            notification.setClub(club);
                            HttpGet.clubs.put(notification.getSender(), club);

                            Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar_notification);
                            holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                request.execute(HttpGet.ROOTASSOCIATION + "/" + notification.getSender() + "?token=" + HttpGet.credentials.getSessionToken());
            } else {
                notification.setClub(club);

                Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar_notification);
                holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                    }
                });
            }
        }

        if (notification.getType().equals("tag")) {
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final User user = new User(new JSONObject(output));
                        notification.setUser(user);

                        // get the drawable of avatar
                        Resources resources = context.getResources();
                        final int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                        Glide.with(context).load(id).into(holder.avatar_notification);

                        holder.avatar_notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", user));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTUSER + "/" + notification.getSender() + "?token=" + HttpGet.credentials.getSessionToken());
        } else if (notification.getType().equals("event")) {
            HttpGet event = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final Event event = new Event(new JSONObject(output));
                        notification.setEvent(event);

                        Glide.with(context).load(HttpGet.IMAGEURL + event.getImage()).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0)).into(holder.thumbnail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            event.execute(HttpGet.ROOTEVENT + "/" + notification.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
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
