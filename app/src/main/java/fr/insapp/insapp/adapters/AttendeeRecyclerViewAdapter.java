package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 10/12/2016.
 */

public class AttendeeRecyclerViewAdapter extends BaseRecyclerViewAdapter<AttendeeRecyclerViewAdapter.UserViewHolder> {

    protected boolean matchParent;

    protected Map<User, Event.PARTICIPATE> users;

    protected OnUserItemClickListener listener;

    public interface OnUserItemClickListener {
        void onUserItemClick(User user);
    }

    public AttendeeRecyclerViewAdapter(Context context, boolean matchParent) {
        this.context = context;
        this.matchParent = matchParent;
        this.users = new LinkedHashMap<>();
    }

    public void setOnItemClickListener(OnUserItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(User user, Event.PARTICIPATE action) {
        this.users.put(user, action);
        this.notifyDataSetChanged();
    }

    @Override
    public AttendeeRecyclerViewAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_thumb_macaroon, parent, false);
        return new UserViewHolder(view, matchParent);
    }

    @Override
    public void onBindViewHolder(AttendeeRecyclerViewAdapter.UserViewHolder holder, int position) {
        final User user = getItem(position);

        if (user != null) {
            // get the drawable of avatarCircleImageView

            final Resources resources = context.getResources();
            final int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
            Glide.with(context).load(id).into(holder.avatar);

            holder.name.setText(user.getName());
            holder.username.setText(user.getUsername());

            holder.bind(user, listener);

            if (users.get(user) == Event.PARTICIPATE.YES)
                holder.hideMacaroon();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private User getItem(int index) {
        return (new ArrayList<>(users.keySet())).get(index);
    }

    public Map<User, Event.PARTICIPATE> getUsers() { return users; }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public CircleImageView macaroon;
        public TextView name;
        public TextView username;

        public UserViewHolder(View view, boolean matchParent) {
            super(view);

            if (matchParent)
                (view.findViewById(R.id.user_thumb_macaroon_layout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            this.avatar = (CircleImageView) view.findViewById(R.id.user_avatar);
            this.macaroon = (CircleImageView) view.findViewById(R.id.macaroon);
            this.name = (TextView) view.findViewById(R.id.user_name);
            this.username = (TextView) view.findViewById(R.id.user_username);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                macaroon.setColorFilter(0xffff9523, PorterDuff.Mode.SRC_ATOP);
        }

        public void bind(final User user, final OnUserItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onUserItemClick(user);
                }
            });
        }

        public void hideMacaroon() {
            macaroon.setVisibility(View.GONE);
        }
    }
}
