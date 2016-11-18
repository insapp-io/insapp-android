package fr.insapp.insapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tomatrocho on 17/11/16.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    protected Context context;

    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
        this.context = context;
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        // not recycled
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.row_comment, viewGroup, false);

        CommentViewHolder holder = (CommentViewHolder) view.getTag();
        if (holder == null) {
            holder = new CommentViewHolder();

            holder.avatar = (CircleImageView) view.findViewById(R.id.avatar_post);
            holder.text = (TextView) view.findViewById(R.id.name_post);

            view.setTag(holder);
        }

        Comment comment = getItem(i);

        holder.avatar.setImageResource(comment.avatar_id);
        holder.text.setText(comment.text);

        return view;
    }

    protected class CommentViewHolder {
        public CircleImageView avatar;
        public TextView text;
    }
}
