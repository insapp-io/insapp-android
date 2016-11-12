package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 30/10/2016.
 */

public class ClubThumbAdapter extends ArrayAdapter<ClubThumb> {

    public Context context;

    public ClubThumbAdapter(Context context, List<ClubThumb> thumbs) {
        super(context, 0, thumbs);
        this.context = context;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
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

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClubActivity.class);
                context.startActivity(intent);
                Toast.makeText(context, "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class ClubThumbHolder {
        public CircleImageView avatar;
        public TextView name;
    }
}
