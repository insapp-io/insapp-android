package fr.insapp.insapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thoma on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private List<Post> posts;

    private OnItemClickListener itemClickListener;

    public PostRecyclerViewAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final Post post = posts.get(position);
        holder.avatar.setImageResource(post.avatar_id);
        holder.title.setText(post.title);
        holder.text.setText(post.text);
        holder.image.setImageResource(post.image_id);
        holder.heart_counter.setText("" + post.heart_counter);
        holder.comment_counter.setText("" + post.comment_counter);
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
        public TextView heart_counter;
        public TextView comment_counter;

        public PostViewHolder(View view) {
            super(view);

            this.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            this.title = (TextView) view.findViewById(R.id.name);
            this.text = (TextView) view.findViewById(R.id.text);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.likeButton = (LikeButton) view.findViewById(R.id.like_button);
            this.heart_counter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
            this.comment_counter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);

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
