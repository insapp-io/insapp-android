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

        return view;
    }

    protected class CommentViewHolder {
        public CircleImageView avatar;
        public TextView text;
    }
}
