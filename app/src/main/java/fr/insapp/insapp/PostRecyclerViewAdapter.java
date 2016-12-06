package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.modeles.Club;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.utility.ImageLoader;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thoma on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private Context context;

    private List<Post> posts;
    private ImageLoader imageLoader;

    private OnItemClickListener itemClickListener;

    public PostRecyclerViewAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.imageLoader = new ImageLoader(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        final Post post = posts.get(position);

        imageLoader.DisplayImage(HttpGet.IMAGEURL + post.getImage(), holder.avatar);//holder.avatar.setImageResource(post.avatar_id);
        holder.title.setText(post.getTitle());
        holder.text.setText(post.getDescription());
        imageLoader.DisplayImage(HttpGet.IMAGEURL + post.getImage(), holder.image);
        holder.likeCounter.setText(Integer.toString(post.getLikes().size()));
        holder.commentCounter.setText(Integer.toString(post.getComments().size()));
        holder.date.setText(new String("il y a " + Operation.displayedDate(post.getDate())));

        // club avatar

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ClubActivity.class));
            }
        });

        // description links

        Linkify.addLinks(holder.text, Linkify.ALL);

        // like button

        holder.likeButton.setLiked(post.postLikedBy(HttpGet.credentials.getUserID()));

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                HttpPost hpp = new HttpPost(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (!output.isEmpty()) {
                            refreshPost(output, holder);
                            //Toast.makeText(context, "liked: " + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                hpp.execute(HttpGet.ROOTURL + "/post/" + post.getId() + "/like/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                HttpDelete hpp = new HttpDelete(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (!output.isEmpty()) {
                            refreshPost(output, holder);
                            //Toast.makeText(context, "unliked: " + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                hpp.execute(HttpGet.ROOTURL + "/post/" + post.getId() + "/like/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PostActivity.class).putExtra("post", post));
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PostActivity.class).putExtra("post", post));
            }
        });
    }

    public void refreshPost(String output, final PostViewHolder holder){
        try {

            JSONObject json = new JSONObject(output);

            Post postRefreshed = new Post(json.getJSONObject("post"));

            holder.likeCounter.setText(Integer.toString(postRefreshed.getLikes().size()));
            holder.commentCounter.setText(Integer.toString(postRefreshed.getComments().size()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        public TextView date;

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
            this.date = (TextView) view.findViewById(R.id.date_post) ;

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
