package fr.insapp.insapp.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.EventActivity
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Comment
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.fragment_event_comments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 25/02/2017.
 */

class CommentsEventFragment : Fragment() {

    private lateinit var commentAdapter: CommentRecyclerViewAdapter

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments

        val bundle = arguments
        if (bundle != null) {
            this.event = bundle.getParcelable("event")
        }

        // adapter

        event.let {
            commentAdapter = CommentRecyclerViewAdapter(event!!.comments, Glide.with(this))
            commentAdapter.setOnCommentItemLongClickListener(EventCommentLongClickListener(context, event!!, commentAdapter))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // edit comment
        comment_event_input.setupComponent()
        comment_event_input.setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // hide keyboard

                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)

                val content = comment_event_input.text.toString()
                comment_event_input.text.clear()

                if (content.isNotBlank()) {
                    val user = Utils.user

                    user?.let {
                        val comment = Comment(null, user.id, content, null, comment_event_input.tags)

                        ServiceGenerator.client.commentEvent(event!!.id, comment)
                            .enqueue(object : Callback<Event> {
                                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                                    if (response.isSuccessful) {
                                        commentAdapter.addComment(comment)
                                        comment_event_input.tags.clear()

                                        Toast.makeText(activity, resources.getText(R.string.write_comment_success), Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(activity, "CommentEditText", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<Event>, t: Throwable) {
                                    Toast.makeText(activity, "CommentEditText", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                }

                return@OnEditorActionListener true
            }

            false
        })

        // recycler view

        recyclerview_comments_event?.setHasFixedSize(true)
        recyclerview_comments_event?.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerview_comments_event?.layoutManager = layoutManager

        recyclerview_comments_event?.adapter = commentAdapter

        // retrieve the avatar of the user

        val user = Utils.user

        val id = resources.getIdentifier(Utils.drawableProfileName(user?.promotion, user?.gender), "drawable", context!!.packageName)
        Glide
            .with(context!!)
            .load(id)
            .apply(RequestOptions.circleCropTransform())
            .transition(withCrossFade())
            .into(comment_event_username_avatar)

        // animation on focus

        comment_event_input?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (activity as EventActivity).appbar_event.setExpanded(false, true)
                (activity as EventActivity).fab_participate_event.hideMenu(false)
            } else {
                val atm = Calendar.getInstance().time

                if (event!!.dateEnd.time >= atm.time) {
                    val handler = Handler()
                    handler.postDelayed({
                        val eventActivity = activity as EventActivity?

                        eventActivity?.fab_participate_event?.showMenu(true)
                    }, 500)
                }
            }
        }
    }

    class EventCommentLongClickListener(
            private val context: Context?,
            private val event: Event,
            private val adapter: CommentRecyclerViewAdapter
    ): CommentRecyclerViewAdapter.OnCommentItemLongClickListener {

        override fun onCommentItemLongClick(comment: Comment) {
            if (context != null) {
                val alertDialogBuilder = AlertDialog.Builder(context)
                val user = Utils.user

                // delete comment

                if (user != null) {
                    if (user.id == comment.user) {
                        alertDialogBuilder
                                .setTitle(context.resources.getString(R.string.delete_comment_action))
                                .setMessage(R.string.delete_comment_are_you_sure)
                                .setCancelable(true)
                                .setPositiveButton(context.getString(R.string.positive_button)) { _: DialogInterface, _: Int ->
                                    val call = ServiceGenerator.client.uncommentEvent(event.id, comment.id!!)
                                    call.enqueue(object : Callback<Event> {
                                        override fun onResponse(call: Call<Event>, response: Response<Event>) {
                                            val event = response.body()
                                            if (response.isSuccessful && event != null) {
                                                adapter.setComments(event.comments)
                                            } else {
                                                Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Event>, t: Throwable) {
                                            Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show()
                                        }
                                    })
                                }
                                .setNegativeButton(context.getString(R.string.negative_button)) { dialogAlert: DialogInterface, _: Int ->
                                    dialogAlert.cancel()
                                }
                                .create().show()
                    }

                    // report comment

                    else {
                        alertDialogBuilder
                                .setTitle(context.getString(R.string.report_comment_action))
                                .setMessage(R.string.report_comment_are_you_sure)
                                .setCancelable(true)
                                .setPositiveButton(context.getString(R.string.positive_button)) { _: DialogInterface, _: Int ->
                                    val call = ServiceGenerator.client.reportComment(event.id, comment.id!!)
                                    call.enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, context.getString(R.string.report_comment_success), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Toast.makeText(context, "EventCommentLongClickListener", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                }
                                .setNegativeButton(context.getString(R.string.negative_button)) { dialogAlert: DialogInterface, _: Int ->
                                    dialogAlert.cancel()
                                }
                                .create().show()
                    }
                }
            }
        }
    }
}
