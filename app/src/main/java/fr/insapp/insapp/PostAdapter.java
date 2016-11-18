package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 29/10/2016.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    protected Context context;

    protected int layout;

    public PostAdapter(Context context, List<Post> posts, int layout) {
        super(context, 0, posts);
        this.context = context;
        this.layout = layout;
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        // not recycled
        if (view == null)
            view = LayoutInflater.from(context).inflate(layout, viewGroup, false);

        PostViewHolder holder = (PostViewHolder) view.getTag();
        if (holder == null) {
            holder = new PostViewHolder();

            if (layout == R.layout.row_post)
                holder.avatar = (CircleImageView) view.findViewById(R.id.avatar);

            holder.title = (TextView) view.findViewById(R.id.name);
            holder.text = (TextView) view.findViewById(R.id.text);
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.likeButton = (LikeButton) view.findViewById(R.id.favorite_button);
            holder.heart_counter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
            holder.comment_counter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);

            view.setTag(holder);
        }

        Post post = getItem(i);

        if (layout == R.layout.row_post)
            holder.avatar.setImageResource(post.avatar_id);

        holder.title.setText(post.title);
        holder.text.setText(post.text);
        holder.image.setImageResource(post.image_id);
        holder.heart_counter.setText("" + post.heart_counter);
        holder.comment_counter.setText("" + post.comment_counter);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostActivity.class);
                context.startActivity(intent);
                Toast.makeText(context, "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        if (layout == R.layout.row_post) {
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ClubActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(context, "" + i, Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "liked " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "unliked " + i, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    protected class PostViewHolder {
        public CircleImageView avatar;
        public TextView title;
        public TextView text;
        public ImageView image;
        public LikeButton likeButton;
        public TextView heart_counter;
        public TextView comment_counter;
    }
}