package fr.insapp.insapp.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Event;

/**
 * Created by thoma on 25/02/2017.
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
        if (HttpGet.credentials.getId().equalsIgnoreCase(comment.getUserId())) {
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.delete_comment_action));
            alertDialogBuilder
                    .setMessage(R.string.delete_comment_are_you_sure)
                    .setCancelable(true)
                    .setPositiveButton(context.getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //setResult(RESULT_OK);

                            HttpDelete delete = new HttpDelete(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    try {
                                        event = new Event(new JSONObject(output));

                                        adapter.setComments(event.getComments());
                                        adapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            delete.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());

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
                            HttpPut report = new HttpPut(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    Toast.makeText(context, context.getString(R.string.report_comment_success), Toast.LENGTH_SHORT).show();
                                }
                            });
                            report.execute(HttpGet.ROOTEVENT + "/report/" + event.getId() + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());
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
