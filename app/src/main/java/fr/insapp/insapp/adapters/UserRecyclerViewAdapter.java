package fr.insapp.insapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.models.User;

/**
 * Created by thoma on 10/12/2016.
 */

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    protected Context context;

    protected List<User> users;

    protected OnUserItemClickListener listener;

    public interface OnUserItemClickListener {
        void onUserItemClick(User user);
    }

    public UserRecyclerViewAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public void setOnItemClickListener(OnUserItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public UserRecyclerViewAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_thumb, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewAdapter.UserViewHolder holder, int position) {
        final User user = users.get(position);

        //holder.avatar.setImageResource(R.drawable.sample_6);
        holder.name.setText(user.getName());
        holder.username.setText(user.getUsername());

        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;
        public TextView username;

        public UserViewHolder(View view) {
            super(view);

            this.avatar = (CircleImageView) view.findViewById(R.id.user_avatar);
            this.name = (TextView) view.findViewById(R.id.user_name);
            this.username = (TextView) view.findViewById(R.id.user_username);
        }

        public void bind(final User user, final OnUserItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onUserItemClick(user);
                }
            });
        }
    }
}
