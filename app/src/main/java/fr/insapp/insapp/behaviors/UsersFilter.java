package fr.insapp.insapp.behaviors;

import android.widget.Filter;

import fr.insapp.insapp.adapters.AutoCompleterAdapter;

/**
 * Created by thomas on 28/02/2017.
 */

public class UsersFilter extends Filter {

    private AutoCompleterAdapter adapter;

    public UsersFilter(AutoCompleterAdapter adapter) {
        super();
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        return null;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

    }
}
