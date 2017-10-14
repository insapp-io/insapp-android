package fr.insapp.insapp.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 25/02/2017.
 */

public class EventCommentLongClickListener implements CommentRecyclerViewAdapter.OnCommentItemLongClickListener {

    private Context context;

    private Event event;
    private CommentRecyclerViewAdapter adapter;

    public EventCommentLongClickListener(Context context, Event event, CommentRecyclerViewAdapter adapter) {
        this.context = context;
        this.event = event;
        this.adapter = adapter;
    }

    @Override
    public void onCommentItemLongClick(final Comment comment) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // delete comment

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();

        if (Utils.getUser().getId().equals(comment.getUser())) {
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.delete_comment_action));
            alertDialogBuilder
                    .setMessage(R.string.delete_comment_are_you_sure)
                    .setCancelable(true)
                    .setPositiveButton(context.getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Event> call = ServiceGenerator.create().uncommentEvent(event.getId(), comment.getId());
                            call.enqueue(new Callback<Event>() {
                                @Override
                                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                                    if (response.isSuccessful()) {
                                        event = response.body();
                                        adapter.setComments(event.getComments());
                                    }
                                    else {
                                        Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                                    Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
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
                            Call<Void> call = ServiceGenerator.create().reportComment(event.getId(), comment.getId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, context.getString(R.string.report_comment_success), Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
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
