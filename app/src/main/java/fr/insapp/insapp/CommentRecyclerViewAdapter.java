package fr.insapp.insapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.modeles.Comment;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 18/11/2016.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    protected List<Comment> comments;

    public CommentRecyclerViewAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        //holder.avatar.setImageResource(comment.avatar_id);
        holder.username.setText(comment.getUser());
        holder.text.setText(comment.getContent());
        holder.date.setText("Il y a " + Operation.displayedDate(comment.getDate()));
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
    }
}
