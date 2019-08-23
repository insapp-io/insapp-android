package fr.insapp.insapp.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 25/02/2017.
 */

public class PostCommentLongClickListener implements CommentRecyclerViewAdapter.OnCommentItemLongClickListener {

    private Context context;

    private Post post;
    private CommentRecyclerViewAdapter adapter;

    public PostCommentLongClickListener(Context context, Post post, CommentRecyclerViewAdapter adapter) {
        this.context = context;
        this.post = post;
        this.adapter = adapter;
    }

    @Override
    public void onCommentItemLongClick(final Comment comment) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // delete comment

        if (Utils.INSTANCE.getUser().getId().equals(comment.getUser())) {
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.delete_comment_action));
            alertDialogBuilder
                    .setMessage(R.string.delete_comment_are_you_sure)
                    .setCancelable(true)
                    .setPositiveButton(context.getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Post> call = ServiceGenerator.create().uncommentPost(post.getId(), comment.getId());
                            call.enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                    if (response.isSuccessful()) {
                                        post = response.body();
                                        adapter.setComments(post.getComments());
                                    }
                                    else {
                                        Toast.makeText(context, "PostCommentLongClickListener", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                                    Toast.makeText(context, "PostCommentLongClickListener", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(context.getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogAlert, int id) {
                            dialogAlert.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        // report comment

        else {
            alertDialogBuilder.setTitle(context.getString(R.string.report_comment_action));
            alertDialogBuilder
                    .setMessage(R.string.report_comment_are_you_sure)
                    .setCancelable(true)
                    .setPositiveButton(context.getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogAlert, int id) {
                            Call<Void> call = ServiceGenerator.create().reportComment(post.getId(), comment.getId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, context.getString(R.string.report_comment_success), Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(context, "PostCommentLongClickListener", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    Toast.makeText(context, "PostCommentLongClickListener", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(context.getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogAlert, int id) {
                            dialogAlert.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
