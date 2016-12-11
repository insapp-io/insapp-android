package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Club;
import fr.insapp.insapp.utility.ImageLoader;

/**
 * Created by thoma on 30/10/2016.
 */

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ClubViewHolder> {

    protected Context context;

    protected List<Club> clubs;
    protected ImageLoader imageLoader;

    protected OnClubItemClickListener listener;

    public interface OnClubItemClickListener {
        void onClubItemClick(Club club);
    }

    public ClubRecyclerViewAdapter(Context context, List<Club> clubs) {
        this.context = context;
        this.clubs = clubs;
        this.imageLoader = new ImageLoader(context);
    }

    public void setOnItemClickListener(OnClubItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_thumb, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClubViewHolder holder, int position) {
        final Club club = clubs.get(position);

        imageLoader.DisplayImage(HttpGet.IMAGEURL + club.getProfilPicture(), holder.avatar);
        holder.name.setText(club.getName());

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
            }
        });

        holder.bind(club, listener);
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;

        public ClubViewHolder(View view) {
            super(view);

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