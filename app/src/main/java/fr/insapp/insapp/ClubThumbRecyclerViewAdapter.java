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
import fr.insapp.insapp.modeles.ClubThumb;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.utility.ImageLoader;

/**
 * Created by thoma on 30/10/2016.
 */

public class ClubThumbRecyclerViewAdapter extends RecyclerView.Adapter<ClubThumbRecyclerViewAdapter.ClubThumbViewHolder> {

    protected Context context;

    protected List<ClubThumb> thumbs;
    protected ImageLoader imageLoader;

    protected OnClubThumbItemClickListener listener;

    public interface OnClubThumbItemClickListener {
        void onClubThumbItemClick(ClubThumb clubThumb);
    }

    public ClubThumbRecyclerViewAdapter(Context context, List<ClubThumb> thumbs) {
        this.context = context;
        this.thumbs = thumbs;
        this.imageLoader = new ImageLoader(context);
    }

    public void setOnItemClickListener(OnClubThumbItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ClubThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_thumb, parent, false);
        return new ClubThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClubThumbViewHolder holder, int position) {
        final ClubThumb thumb = thumbs.get(position);

        imageLoader.DisplayImage(HttpGet.IMAGEURL + thumb.getProfilPicture(), holder.avatar);
        holder.name.setText(thumb.getName());

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ClubActivity.class));
            }
        });
        holder.bind(thumb, listener);
    }

    @Override
    public int getItemCount() {
        return thumbs.size();
    }

    public static class ClubThumbViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;

        public ClubThumbViewHolder(View view) {
            super(view);
            this.avatar = (CircleImageView) view.findViewById(R.id.club_thumb);
            this.name = (TextView) view.findViewById(R.id.clubname);
        }

        public void bind(final ClubThumb clubThumb, final OnClubThumbItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClubThumbItemClick(clubThumb);
                }
            });
        }
    }
}
