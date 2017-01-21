package fr.insapp.insapp.adapters;

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

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.ClubActivity;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thoma on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;

    private int layout;

    private OnPostItemClickListener listener;

    public interface OnPostItemClickListener {
        void onPostItemClick(Post post);
    }

    public PostRecyclerViewAdapter(Context context, int layout) {
        this.context = context;
        this.posts = new ArrayList<>();
        this.layout = layout;
    }

    public void setOnItemClickListener(OnPostItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Post post) {
        this.posts.add(post);
        this.notifyDataSetChanged();
    }

    public void updatePost(int id, Post post){
        this.posts.set(id, post);
        notifyItemChanged(id);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        final Post post = posts.get(position);

        holder.title.setText(post.getTitle());
        holder.date.setText(new String("il y a " + Operation.displayedDate(post.getDate())));

        if (layout != R.layout.row_post) {
            Glide.with(context).load(HttpGet.IMAGEURL + post.getImage()).into(holder.image);
            holder.text.setText(post.getDescription());
            holder.likeCounter.setText(Integer.toString(post.getLikes().size()));
            holder.commentCounter.setText(Integer.toString(post.getComments().size()));
        }

        // club avatar

        if (layout != R.layout.post) {
            final Club club = HttpGet.clubs.get(post.getAssociation());

            if (club == null) {

                HttpGet request = new HttpGet(new AsyncResponse() {

                    public void processFinish(String output) {
                        if (!output.isEmpty()) {
                            try {
                                JSONObject jsonobject = new JSONObject(output);

                                final Club club = new Club(jsonobject);
                                HttpGet.clubs.put(club.getId(), club);

                                // glide

                                if (layout != R.layout.post)
                                    Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar);

                                holder.avatar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                request.execute(HttpGet.ROOTASSOCIATION + "/"+ post.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());

            }
            else {
                // glide
                if (layout != R.layout.post)
                    Glide.with(context).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(holder.avatar);

                holder.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                    }
                });
            }

        }

        // description links

        if (layout != R.layout.row_post) {
            Linkify.addLinks(holder.text, Linkify.ALL);
            Utils.stripUnderlines(holder.text);
        }

        // like button

        if (layout != R.layout.row_post) {
            holder.likeButton.setLiked(post.postLikedBy(HttpGet.credentials.getUserID()));

            holder.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    HttpPost hpp = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (!output.isEmpty()) {
                                refreshPost(output, holder);
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
                            }
                        }
                    });

                    hpp.execute(HttpGet.ROOTURL + "/post/" + post.getId() + "/like/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
            });

            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, PostActivity.class).putExtra("post", post));
                }
            });
        }

        holder.bind(post, listener);
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

    public List<Post> getPosts() {
        return posts;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
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

            if (layout != R.layout.post)
                this.avatar = (CircleImageView) view.findViewById(R.id.avatar_club_post);

            this.title = (TextView) view.findViewById(R.id.name_post);
            this.date = (TextView) view.findViewById(R.id.date_post);

            if (layout != R.layout.row_post) {
                this.text = (TextView) view.findViewById(R.id.post_text);
                this.image = (ImageView) view.findViewById(R.id.image);
                this.likeButton = (LikeButton) view.findViewById(R.id.like_button);
                this.likeCounter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
                this.commentButton = (ImageButton) view.findViewById(R.id.comment_button);
                this.commentCounter = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);
            }
        }

        public void bind(final Post post, final OnPostItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPostItemClick(post);
                }
            });
        }
    }
}
