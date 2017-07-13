package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.ProfileActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thoma on 18/11/2016.
 */

public class CommentRecyclerViewAdapter extends BaseRecyclerViewAdapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    protected List<Comment> comments;

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
            int posEnd = posStart + tag.getName().length();

            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    HttpGet request = new HttpGet(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            try {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", new User(new JSONObject(output))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    /*
                    request.execute(HttpGet.ROOTUSER + "/" + tag.getUser() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                    */
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

        holder.dateTextView.setText(String.format(context.getResources().getString(R.string.ago), Operation.displayedDate(comment.getDate())));

        // user

        Call<User> call = ServiceGenerator.create().getUserFromId(comment.getUserId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    final User user = response.body();

                    final int id = context.getResources().getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                    Glide.with(context).load(id).into(holder.avatarCircleImageView);

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
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "CommentRecyclerViewAdapter", Toast.LENGTH_LONG).show();
            }
        });

        holder.bind(comment, listener);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarCircleImageView;
        public TextView usernameTextView;
        public TextView contentTextView;
        public TextView dateTextView;

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
    }
}
