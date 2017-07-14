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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends BaseRecyclerViewAdapter<PostRecyclerViewAdapter.PostViewHolder> {

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

    public void updatePost(int position, Post post) {
        this.posts.set(position, post);
        notifyItemChanged(position);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        final Post post = posts.get(position);

        holder.getTitleTextView().setText(post.getTitle());
        holder.getDateTextView().setText(String.format(context.getResources().getString(R.string.ago), Operation.displayedDate(post.getDate())));

        // available layouts are row_post, post or post_with_avatars

        if (layout == R.layout.row_post) {
            Glide
                    .with(context)
                    .load(HttpGet.IMAGEURL + post.getImage())
                    .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 8, 0))
                    .crossFade()
                    .into(holder.getImageView());
        }
        else {
            Glide
                    .with(context)
                    .load(HttpGet.IMAGEURL + post.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(holder.getImageView());

            holder.getContentTextView().setText(post.getDescription());
            holder.getLikeCounterTextView().setText(String.format(Locale.FRANCE, "%d", post.getLikes().size()));
            holder.getCommentCounterTextView().setText(String.format(Locale.FRANCE, "%d", post.getComments().size()));

            // club avatar

            if (layout == R.layout.post_with_avatars) {
                Call<Club> call = ServiceGenerator.create().getClubFromId(post.getAssociation());
                call.enqueue(new Callback<Club>() {
                    @Override
                    public void onResponse(Call<Club> call, Response<Club> response) {
                        if (response.isSuccessful()) {
                            final Club club = response.body();

                            Glide
                                    .with(context)
                                    .load(HttpGet.IMAGEURL + club.getProfilPicture())
                                    .crossFade()
                                    .into(holder.getAvatarCircleImageView());

                            // listener

                            holder.getAvatarCircleImageView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(context, ClubActivity.class).putExtra("club", club));
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Club> call, Throwable t) {
                        Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                    }
                });
            }

            // description links

            Linkify.addLinks(holder.getContentTextView(), Linkify.ALL);
            Utils.convertToLinkSpan(context, holder.getContentTextView());

            // like button

            final String userId = new Gson().fromJson(context.getSharedPreferences("Credentials", MODE_PRIVATE).getString("session", ""), SessionCredentials.class).getUser().getId();

            holder.getLikeButton().setLiked(post.isPostLikedBy(userId));

            holder.getLikeButton().setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Call<PostInteraction> call = ServiceGenerator.create().likePost(post.getId(), userId);
                    call.enqueue(new Callback<PostInteraction>() {
                        @Override
                        public void onResponse(Call<PostInteraction> call, Response<PostInteraction> response) {
                            if (response.isSuccessful()) {
                                updatePost(position, response.body().getPost());
                            }
                            else {
                                Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PostInteraction> call, Throwable t) {
                            Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Call<PostInteraction> call = ServiceGenerator.create().dislikePost(post.getId(), userId);
                    call.enqueue(new Callback<PostInteraction>() {
                        @Override
                        public void onResponse(Call<PostInteraction> call, Response<PostInteraction> response) {
                            if (response.isSuccessful()) {
                                updatePost(position, response.body().getPost());
                            }
                            else {
                                Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PostInteraction> call, Throwable t) {
                            Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            // comment button

            holder.getCommentButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, PostActivity.class).putExtra("post", post));
                }
            });
        }

        holder.bind(post, listener);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public List<Post> getPosts() {
        return posts;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatarCircleImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private ImageView imageView;
        private LikeButton likeButton;
        private TextView likeCounterTextView;
        private ImageButton commentButton;
        private TextView commentCounterTextView;
        private TextView dateTextView;

        private PostViewHolder(View view) {
            super(view);

            if (layout != R.layout.post) {
                this.avatarCircleImageView = (CircleImageView) view.findViewById(R.id.avatar_club_post);
            }

            this.titleTextView = (TextView) view.findViewById(R.id.name_post);
            this.dateTextView = (TextView) view.findViewById(R.id.date_post);

            if (layout == R.layout.row_post) {
                this.imageView = (ImageView) view.findViewById(R.id.thumbnail_post);
            }
            else {
                this.imageView = (ImageView) view.findViewById(R.id.image);
            }

            if (layout != R.layout.row_post) {
                this.contentTextView = (TextView) view.findViewById(R.id.post_text);
                this.likeButton = (LikeButton) view.findViewById(R.id.like_button);
                this.likeCounterTextView = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
                this.commentButton = (ImageButton) view.findViewById(R.id.comment_button);
                this.commentCounterTextView = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);
            }
        }

        private void bind(final Post post, final OnPostItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPostItemClick(post);
                }
            });
        }

        public CircleImageView getAvatarCircleImageView() {
            return avatarCircleImageView;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getContentTextView() {
            return contentTextView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public LikeButton getLikeButton() {
            return likeButton;
        }

        public TextView getLikeCounterTextView() {
            return likeCounterTextView;
        }

        public ImageButton getCommentButton() {
            return commentButton;
        }

        public TextView getCommentCounterTextView() {
            return commentCounterTextView;
        }

        public TextView getDateTextView() {
            return dateTextView;
        }
    }
}
