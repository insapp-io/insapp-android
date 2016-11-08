package com.example.tomatrocho.insapp_material;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 30/10/2016.
 */

public class ClubThumbAdapter extends ArrayAdapter<ClubThumb> {

    public ClubThumbAdapter(Context context, List<ClubThumb> thumbs) {
        super(context, 0, thumbs);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // not recycled
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.club_thumb, viewGroup, false);

        ClubThumbHolder holder = (ClubThumbHolder) view.getTag();
        if (holder == null) {
            holder = new ClubThumbHolder();
            holder.avatar = (CircleImageView) view.findViewById(R.id.club_thumb);
            holder.name = (TextView) view.findViewById(R.id.clubname);
            view.setTag(holder);
        }

        ClubThumb thumb = getItem(i);

        holder.avatar.setImageResource(thumb.avatar_id);
        holder.name.setText(thumb.name);

        return view;
    }

    private class ClubThumbHolder {
        public CircleImageView avatar;
        public TextView name;
    }
}
