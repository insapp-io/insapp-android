package fr.insapp.insapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thomas on 27/02/2017.
 */

public class AutoCompleterAdapter extends ArrayAdapter<User> implements Filterable {

    private Context context;

    private ArrayList<User> filteredUsers;
    private ArrayList<User> taggedUsers;

    public AutoCompleterAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;

        this.filteredUsers = new ArrayList<>();
        this.taggedUsers = new ArrayList<>();
    }

    public int getCount() {
        return filteredUsers.size();
    }

    public User getItem(int index) {
        return filteredUsers.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown, parent, false);
        }

        final User user = getItem(position);
        final Resources resources = context.getResources();

        ((TextView) convertView.findViewById(R.id.dropdown_textview)).setText(String.format(resources.getString(R.string.tag), user.getUsername()));

        // get the drawable of avatarCircleImageView
        final int id = resources.getIdentifier(Operation.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", context.getPackageName());
        Glide.with(context).load(id).into(((CircleImageView) convertView.findViewById(R.id.dropdown_avatar)));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("terms", constraint);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // a class that queries a web API, parses the data and returns an ArrayList<Style>
                    HttpPost request = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            try {
                                ArrayList<User> users = new ArrayList<>();

                                JSONObject json = new JSONObject(output);
                                JSONArray jsonArray = json.getJSONArray("users");

                                if (jsonArray != null) {
                                    for (int i = 0; i < Math.min(jsonArray.length(), 10); i++) {
                                        final User user = new User(jsonArray.getJSONObject(i));

                                        taggedUsers.add(user);
                                        users.add(user);
                                    }
                                }

                                filterResults.values = users;
                                filterResults.count = users.size();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    /*
                    try {
                        request.execute(HttpGet.ROOTSEARCHUSERS + "?token=" + HttpGet.sessionCredentials.getSessionToken(), jsonObject.toString()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    */
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    filteredUsers = (ArrayList<User>) results.values;
                    notifyDataSetChanged();
                } else
                    notifyDataSetInvalidated();
            }
        };
    }

    public ArrayList<User> getTaggedUsers() {
        return taggedUsers;
    }
}
