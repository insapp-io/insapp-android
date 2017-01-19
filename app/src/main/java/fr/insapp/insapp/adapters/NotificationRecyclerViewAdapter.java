package fr.insapp.insapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Notification;

/**
 * Created by thoma on 11/12/2016.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    protected List<Notification> notifications;

    protected OnNotificationItemClickListener listener;

    public interface OnNotificationItemClickListener {
        void onNotificationItemClick(Notification notification);
    }

    public NotificationRecyclerViewAdapter(List<Notification> notifications) {
        this.notifications = notifications;
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
    public void onBindViewHolder(NotificationRecyclerViewAdapter.NotificationViewHolder holder, int position) {
        final Notification notification = notifications.get(position);

        holder.text.setText(notification.getContent());
        holder.date.setText(notification.getContent());

        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public TextView date;

        public NotificationViewHolder(View view) {
            super(view);

            this.text = (TextView) view.findViewById(R.id.notification_text);
            this.date = (TextView) view.findViewById(R.id.notification_date);
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
