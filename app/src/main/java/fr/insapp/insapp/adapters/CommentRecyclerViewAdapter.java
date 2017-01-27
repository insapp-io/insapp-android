package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.ProfileActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Tag;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 18/11/2016.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    private Context context;
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
                    request.execute(HttpGet.ROOTUSER + "/" + tag.getUser() + "?token=" + HttpGet.credentials.getSessionToken());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            spannableString.setSpan(span, posStart, posEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.text.setText(spannableString);
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());
        holder.text.setEnabled(true);

        holder.date.setText("il y a " + Operation.displayedDate(comment.getDate()));

        // user

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject json = null;
                try {
                    json = new JSONObject(output);
                    final User user = new User(json);

                    // Get the drawable of avatar
                    Resources resources = context.getResources();
                    final int id = resources.getIdentifier(Operation.drawableProfilName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
                    Glide.with(context).load(id).into(holder.avatar);

                    holder.username.setText("@" + user.getUsername());

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", user));
                        }
                    };

                    holder.avatar.setOnClickListener(listener);
                    holder.username.setOnClickListener(listener);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.execute(HttpGet.ROOTUSER + "/" + comment.getUserId() + "?token=" + HttpGet.credentials.getSessionToken());

        holder.bind(comment, listener);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView username;
        public TextView text;
        public TextView date;

        public CommentViewHolder(View view) {
            super(view);

            this.avatar = (CircleImageView) view.findViewById(R.id.club_avatar_post);
            this.username = (TextView) view.findViewById(R.id.username_comment);
            this.text = (TextView) view.findViewById(R.id.text_comment);
            this.date = (TextView) view.findViewById(R.id.date_comment);
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
