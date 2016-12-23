package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Comment;
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

        holder.text.setText(comment.getContent());
        holder.date.setText("Il y a " + Operation.displayedDate(comment.getDate()));

        // Get the user
        HttpGet get = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject json = null;
                try {
                    json = new JSONObject(output);
                    final User user = new User(json);

                    // Get the drawable of avatar
                    Resources resources = context.getResources();
                    int id = resources.getIdentifier(Operation.drawableProfilName(user), "drawable", context.getPackageName());

                    Drawable dr = ContextCompat.getDrawable(context, id);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

                    // Resize the bitmap
                    Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));

                    holder.avatar.setImageDrawable(d);
                    holder.username.setText("@" + user.getUsername());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        get.execute(HttpGet.ROOTUSER + "/" + comment.getUserId() + "?token=" + HttpGet.credentials.getSessionToken());

        holder.bind(comment, listener);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
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
