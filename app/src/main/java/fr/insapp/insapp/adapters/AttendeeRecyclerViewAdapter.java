package fr.insapp.insapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Utils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thomas on 10/12/2016.
 */

public class AttendeeRecyclerViewAdapter extends BaseRecyclerViewAdapter<AttendeeRecyclerViewAdapter.UserViewHolder> {

    private RequestManager requestManager;

    private boolean matchParent;

    private Map<User, Event.PARTICIPATE> users;

    private OnUserItemClickListener listener;

    public interface OnUserItemClickListener {
        void onUserItemClick(User user);
    }

    public AttendeeRecyclerViewAdapter(Context context, RequestManager requestManager, boolean matchParent) {
        this.context = context;
        this.requestManager = requestManager;
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
            // get the drawable of avatar

            final int id = context.getResources().getIdentifier(Utils.INSTANCE.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
            requestManager
                    .load(id)
                    .apply(RequestOptions.circleCropTransform())
                    .transition(withCrossFade())
                    .into(holder.avatar);

            holder.name.setText(user.getName());
            holder.username.setText(user.getUsername());

            holder.bind(user, listener);

            if (users.get(user) == Event.PARTICIPATE.YES) {
                holder.hideMacaroon();
            }
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
        public ImageView avatar;
        public ImageView macaroon;
        public TextView name;
        public TextView username;

        public UserViewHolder(View view, boolean matchParent) {
            super(view);

            if (matchParent)
                (view.findViewById(R.id.user_thumb_macaroon_layout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            this.avatar = view.findViewById(R.id.user_avatar);
            this.macaroon = view.findViewById(R.id.macaroon);
            this.name = view.findViewById(R.id.user_name);
            this.username = view.findViewById(R.id.user_username);

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
