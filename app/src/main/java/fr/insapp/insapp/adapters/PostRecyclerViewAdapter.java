package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.components.RatioImageView;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thomas on 19/11/2016.
 */

public class PostRecyclerViewAdapter extends BaseRecyclerViewAdapter<PostRecyclerViewAdapter.PostViewHolder> {

    private RequestManager requestManager;

    private List<Post> posts;

    private int layout;

    private OnPostItemClickListener listener;

    public interface OnPostItemClickListener {
        void onPostItemClick(Post post);
    }

    public PostRecyclerViewAdapter(Context context, RequestManager requestManager, int layout) {
        this.context = context;
        this.requestManager = requestManager;
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
        holder.getDateTextView().setText(Utils.INSTANCE.displayedDate(post.getDate()));

        // available layouts are row_post, post or post_with_avatars

        if (layout != R.layout.post) {

            // club avatar

            Call<Club> call = ServiceGenerator.create().getClubFromId(post.getAssociation());
            call.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                    if (response.isSuccessful()) {
                        final Club club = response.body();

                        requestManager
                                .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
                                .transition(withCrossFade())
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
                public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                    Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (layout == R.layout.row_post) {
            requestManager
                    .load(ServiceGenerator.CDN_URL + post.getImage())
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8)))
                    .transition(withCrossFade())
                    .into(holder.getImageView());
        }
        else {
            holder.getPlaceholderImageView().setImageSize(post.getImageSize());

            requestManager
                    .load(ServiceGenerator.CDN_URL + post.getImage())
                    .transition(withCrossFade())
                    .into(holder.getImageView());

            holder.getContentTextView().setText(post.getDescription());
            holder.getLikeCounterTextView().setText(String.format(Locale.FRANCE, "%d", post.getLikes().size()));
            holder.getCommentCounterTextView().setText(String.format(Locale.FRANCE, "%d", post.getComments().size()));

            // description links

            Linkify.addLinks(holder.getContentTextView(), Linkify.ALL);
            Utils.INSTANCE.convertToLinkSpan(context, holder.getContentTextView());

            // like button

            final String userId = Utils.INSTANCE.getUser().getId();

            holder.getLikeButton().setLiked(post.isPostLikedBy(userId));

            holder.getLikeButton().setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Call<PostInteraction> call = ServiceGenerator.create().likePost(post.getId(), userId);
                    call.enqueue(new Callback<PostInteraction>() {
                        @Override
                        public void onResponse(@NonNull Call<PostInteraction> call, @NonNull Response<PostInteraction> response) {
                            if (response.isSuccessful()) {
                                updatePost(position, response.body().getPost());
                            }
                            else {
                                Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PostInteraction> call, @NonNull Throwable t) {
                            Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Call<PostInteraction> call = ServiceGenerator.create().dislikePost(post.getId(), userId);
                    call.enqueue(new Callback<PostInteraction>() {
                        @Override
                        public void onResponse(@NonNull Call<PostInteraction> call, @NonNull Response<PostInteraction> response) {
                            if (response.isSuccessful()) {
                                updatePost(position, response.body().getPost());
                            }
                            else {
                                Toast.makeText(context, "PostRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PostInteraction> call, @NonNull Throwable t) {
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
        private RatioImageView placeholderImageView;
        private LikeButton likeButton;
        private TextView likeCounterTextView;
        private ImageButton commentButton;
        private TextView commentCounterTextView;
        private TextView dateTextView;

        private PostViewHolder(View view) {
            super(view);

            // R.layout.row_post || R.layout.post_with_avatar

            if (layout != R.layout.post) {
                this.avatarCircleImageView = (CircleImageView) view.findViewById(R.id.avatar_club_post);
            }

            this.titleTextView = (TextView) view.findViewById(R.id.name_post);
            this.dateTextView = (TextView) view.findViewById(R.id.date_post);

            // R.layout.row_post

            if (layout == R.layout.row_post) {
                this.imageView = (ImageView) view.findViewById(R.id.thumbnail_post);
                this.placeholderImageView = null;
            }

            // R.layout.post || R.layout.post_with_avatar

            else {
                this.imageView = (ImageView) view.findViewById(R.id.image);
                this.placeholderImageView = (RatioImageView) view.findViewById(R.id.placeholder);
                this.contentTextView = (TextView) view.findViewById(R.id.post_text);
                this.likeButton = (LikeButton) view.findViewById(R.id.like_button);
                this.likeCounterTextView = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.heart_counter);
                this.commentButton = (ImageButton) view.findViewById(R.id.comment_button);
                this.commentCounterTextView = (TextView) view.findViewById(R.id.reactions).findViewById(R.id.comment_counter);
            }
        }

        private void bind(final Post post, final OnPostItemClickListener listener) {

            // hide image if necessary

            if (post.getImageSize() == null || post.getImage().isEmpty()) {
                placeholderImageView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }

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

        public RatioImageView getPlaceholderImageView() {
            return placeholderImageView;
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
