package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 11/12/2016.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    private Context context;
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
        //holder.text.setText(Html.fromHtml("<FONT color='BLACK'>" + notification.getMessage() + "</FONT> <FONT color='GREY'>" + Operation.displayedDate(notification.getDate()) + "</FONT>"));

        holder.date.setText(Operation.displayedDate(notification.getDate()));
/*
        if (!notification.isSeen()) {
            HttpDelete seen = new HttpDelete(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            seen.execute(HttpGet.ROOTNOTIFICATION + "/" + HttpGet.credentials.getUserID() + "/" + notification.getId() + "?token=" + HttpGet.credentials.getSessionToken());
        }
*/
        if (notification.getType().equals("tag") || notification.getType().equals("post")) {

            HttpGet post = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final Post post = new Post(new JSONObject(output));

                        //Glide.with(context).load(HttpGet.IMAGEURL + post.getImage()).into(holder.image_notification);

                        if (notification.getType().equals("tag")) {

                            /*childLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i = new Intent(getActivity(), Comments.class);
                                    i.putExtra("post", post);
                                    i.putExtra("taggedCommentID", notification.getCommentID());
                                    startActivity(i);
                                }
                            });*/
                        }

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
                            HttpGet.clubs.put(notification.getSender(), club);

                            Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar_notification);
/*
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), AssociationProfil.class);
                                    i.putExtra("asso", asso);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                }
                            });
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                request.execute(HttpGet.ROOTASSOCIATION + "/" + notification.getSender() + "?token=" + HttpGet.credentials.getSessionToken());
            } else {

                Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar_notification);

                /*img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), AssociationProfil.class);
                        i.putExtra("asso", asso);
                        startActivity(i);
                    }
                });*/

            }
        }


        if (notification.getType().equals("tag")) {
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final User user = new User(new JSONObject(output));

                        // Get the drawable of avatar
                        Resources resources = context.getResources();
                        int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());

                        Drawable dr = ContextCompat.getDrawable(context, id);
                        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

                        // Resize the bitmap
                        Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));

                        holder.avatar_notification.setImageDrawable(d);

                        /*img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), ProfilActivity.class);
                                i.putExtra("user", user);
                                startActivity(i);
                            }
                        });*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTUSER + "/" + notification.getSender() + "?token=" + HttpGet.credentials.getSessionToken());

        } else if (notification.getType().equals("post")) {

            HttpGet post = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        Post post = new Post(new JSONObject(output));

                        //Glide.with(context).load(HttpGet.IMAGEURL + post.getImage()).into(holder.image_notification);
/*
                        childLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(getActivity(), PostActivity.class);
                                i.putExtra("notification", notification);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                            }
                        });*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            post.execute(HttpGet.ROOTPOST + "/" + notification.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
        } else if (notification.getType().equals("event")) {

            HttpGet event = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        final Event event = new Event(new JSONObject(output));

                        //Glide.with(context).load(HttpGet.IMAGEURL + event.getImage()).into(holder.image_notification);
/*
                        childLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(getActivity(), EventProfil.class);
                                i.putExtra("event", event);
                                i.putExtra("asso", event.getAssociation());
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            }
                        });
*/
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
        public ImageView image_notification;

        public NotificationViewHolder(View view) {
            super(view);

            this.text = (TextView) view.findViewById(R.id.notification_text);
            this.date = (TextView) view.findViewById(R.id.notification_date);
            this.avatar_notification = (CircleImageView) view.findViewById(R.id.avatar_notification);
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
