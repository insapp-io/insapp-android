package fr.insapp.insapp.adapters;

import android.content.Context;
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
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;

/**
 * Created by thoma on 30/10/2016.
 */

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ClubViewHolder> {

    protected Context context;
    protected boolean matchParent;

    protected List<Club> clubs;

    protected OnClubItemClickListener listener;

    public interface OnClubItemClickListener {
        void onClubItemClick(Club club);
    }

    public ClubRecyclerViewAdapter(Context context, boolean matchParent) {
        this.context = context;
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

    @Override
    public ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_thumb, parent, false);
        return new ClubViewHolder(view, matchParent);
    }

    @Override
    public void onBindViewHolder(ClubViewHolder holder, int position) {
        final Club club = clubs.get(position);

        holder.name.setText(club.getName());

        // glide

        Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar);

        holder.bind(club, listener);
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public List<Club> getClubs() { return clubs; }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;

        public ClubViewHolder(View view, boolean matchParent) {
            super(view);

            if (matchParent)
                (view.findViewById(R.id.club_thumb_layout)).getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            this.avatar = (CircleImageView) view.findViewById(R.id.club_avatar);
            this.name = (TextView) view.findViewById(R.id.club_name);
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