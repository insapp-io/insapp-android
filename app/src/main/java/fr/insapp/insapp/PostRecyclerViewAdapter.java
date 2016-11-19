package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private List<Post> posts;

    private OnItemClickListener itemClickListener;

    private Context context;

    public PostRecyclerViewAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, final int position) {
        final Post post = posts.get(position);

        holder.avatar.setImageResource(post.avatar_id);
        holder.title.setText(post.title);
        holder.text.setText(post.text);
        holder.image.setImageResource(post.image_id);
        holder.likeCounter.setText("" + post.heart_counter);
        holder.commentCounter.setText("" + post.comment_counter);

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ClubActivity.class));
            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "liked: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "unliked: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PostActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CircleImageView avatar;
        public TextView title;
        public TextView text;
        public ImageView image;
        public LikeButton likeButton;
        public TextView likeCounter;
        public ImageButton commentButton;
        public TextView commentCounter;

        public PostViewHolder(View view) {
            super(view);

            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.title = (TextView) view.findViewById(R.id.name);
            this.text = (TextView) view.findViewById(R.id.text);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.likeButton = (LikeButton) view.findViewById(R.id.like_button);
            this.likeCounter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
            this.commentButton = (ImageButton) view.findViewById(R.id.comment_button);
            this.commentCounter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(view, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
