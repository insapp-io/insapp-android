package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.ProfileActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 18/11/2016.
 */

public class CommentRecyclerViewAdapter extends BaseRecyclerViewAdapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    private List<Comment> comments;

    private OnCommentItemLongClickListener listener;

    public interface OnCommentItemLongClickListener {
        void onCommentItemLongClick(Comment comment);
    }

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setOnItemLongClickListener(OnCommentItemLongClickListener listener) {
        this.listener = listener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        // tagging

        String str = comment.getContent();
        SpannableString spannableString = new SpannableString(str);

        for (final Tag tag : comment.getTags()) {
            int posStart = str.indexOf(tag.getName());

            if (posStart < 0) {
                posStart = 0;
            }

            final int posEnd = posStart + tag.getName().length();

            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Call<User> call = ServiceGenerator.create().getUserFromId(tag.getUser());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.isSuccessful()) {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", response.body()));
                            }
                            else {
                                Toast.makeText(context, "CommentRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Toast.makeText(context, "CommentRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            spannableString.setSpan(span, posStart, posEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.contentTextView.setText(spannableString);
        holder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.contentTextView.setEnabled(true);

        holder.dateTextView.setText(Utils.displayedDate(comment.getDate()));

        // user

        Call<User> call = ServiceGenerator.create().getUserFromId(comment.getUser());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    final User user = response.body();

                    final int id = context.getResources().getIdentifier(Utils.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                    Glide
                            .with(context)
                            .load(id)
                            .crossFade()
                            .into(holder.avatarCircleImageView);

                    holder.usernameTextView.setText(String.format(context.getResources().getString(R.string.tag), user.getUsername()));

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", user));
                        }
                    };

                    holder.avatarCircleImageView.setOnClickListener(listener);
                    holder.usernameTextView.setOnClickListener(listener);
                }
                else {
                    Toast.makeText(context, "CommentRecyclerViewAdapter", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(context, "CommentRecyclerViewAdapter", Toast.LENGTH_LONG).show();
            }
        });

        holder.bind(comment, listener);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        this.notifyDataSetChanged();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }

        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatarCircleImageView;
        private TextView usernameTextView;
        private TextView contentTextView;
        private TextView dateTextView;

        public CommentViewHolder(View view) {
            super(view);

            this.avatarCircleImageView = (CircleImageView) view.findViewById(R.id.username_avatar);
            this.usernameTextView = (TextView) view.findViewById(R.id.username_comment);
            this.contentTextView = (TextView) view.findViewById(R.id.content_comment);
            this.dateTextView = (TextView) view.findViewById(R.id.date_comment);
        }

        public void bind(final Comment comment, final OnCommentItemLongClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onCommentItemLongClick(comment);
                    return true;
                }
            });
        }

        public CircleImageView getAvatarCircleImageView() {
            return avatarCircleImageView;
        }

        public TextView getUsernameTextView() {
            return usernameTextView;
        }

        public TextView getContentTextView() {
            return contentTextView;
        }

        public TextView getDateTextView() {
            return dateTextView;
        }
    }
}
