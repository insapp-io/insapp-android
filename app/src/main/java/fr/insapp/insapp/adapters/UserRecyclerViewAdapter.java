package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 10/12/2016.
 */

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    protected Context context;
    protected boolean matchParent;

    protected List<User> users;

    protected OnUserItemClickListener listener;

    public interface OnUserItemClickListener {
        void onUserItemClick(User user);
    }

    public UserRecyclerViewAdapter(Context context, boolean matchParent) {
        this.context = context;
        this.matchParent = matchParent;
        this.users = new ArrayList<>();
    }

    public void setOnItemClickListener(OnUserItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(User user) {
        this.users.add(user);
        this.notifyDataSetChanged();
    }

    @Override
    public UserRecyclerViewAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_thumb, parent, false);
        return new UserViewHolder(view, matchParent);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewAdapter.UserViewHolder holder, int position) {
        final User user = users.get(position);

        // get the drawable of avatar
        Resources resources = context.getResources();
        int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
        Glide.with(context).load(id).into(holder.avatar);

        holder.name.setText(user.getName());
        holder.username.setText(user.getUsername());

        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public List<User> getUsers() { return users; }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;
        public TextView username;

        public UserViewHolder(View view, boolean matchParent) {
            super(view);

            if (matchParent)
                (view.findViewById(R.id.user_thumb_layout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

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
