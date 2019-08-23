package fr.insapp.insapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thomas on 30/10/2016.
 */

public class ClubRecyclerViewAdapter extends BaseRecyclerViewAdapter<ClubRecyclerViewAdapter.ClubViewHolder> {

    private RequestManager requestManager;

    private boolean matchParent;

    protected List<Club> clubs;

    private OnClubItemClickListener listener;

    public interface OnClubItemClickListener {
        void onClubItemClick(Club club);
    }

    public ClubRecyclerViewAdapter(Context context, RequestManager requestManager, boolean matchParent) {
        this.context = context;
        this.requestManager = requestManager;
        this.matchParent = matchParent;
        this.clubs = new ArrayList<>();
    }

    public void setOnItemClickListener(OnClubItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Club club) {
        this.clubs.add(club);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_thumb, parent, false);
        return new ClubViewHolder(view, matchParent);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        final Club club = clubs.get(position);

        holder.name.setText(club.getName());

        // glide

        requestManager
            .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
            .apply(RequestOptions.circleCropTransform())
            .transition(withCrossFade())
            .into(holder.avatar);

        holder.bind(club, listener);
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public List<Club> getClubs() { return clubs; }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView name;

        public ClubViewHolder(View view, boolean matchParent) {
            super(view);

            if (matchParent)
                (view.findViewById(R.id.club_thumb_layout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            this.avatar = view.findViewById(R.id.club_avatar);
            this.name = view.findViewById(R.id.club_name);
        }

        public void bind(final Club club, final OnClubItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClubItemClick(club);
                }
            });
        }
    }
}