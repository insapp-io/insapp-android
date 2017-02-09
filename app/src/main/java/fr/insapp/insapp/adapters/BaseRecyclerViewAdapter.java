package fr.insapp.insapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by thomas on 01/02/2017.
 */

public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;
}