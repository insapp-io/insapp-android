package fr.insapp.insapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 29/10/2016.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    public PostAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        // not recycled
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_post, viewGroup, false);

        PostViewHolder holder = (PostViewHolder) view.getTag();
        if (holder == null) {
            holder = new PostViewHolder();
            holder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            holder.title = (TextView) view.findViewById(R.id.name);
            holder.text = (TextView) view.findViewById(R.id.text);
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        }

        Post post = getItem(i);

        holder.avatar.setImageResource(post.avatar_id);
        holder.title.setText(post.title);
        holder.text.setText(post.text);
        holder.image.setImageResource(post.image_id);

        return view;
    }

    private class PostViewHolder {
        public CircleImageView avatar;
        public TextView title;
        public TextView text;
        public ImageView image;
    }
}