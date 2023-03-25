package com.example.qrky;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qrky.placeholder.PlaceholderContent;
import com.example.qrky.placeholder.PlaceholderContent.PlaceholderItem;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

/**
 * Adapter for a RecyclerView that displays a list of PlaceholderItem objects.
 * Supports filtering the list based on a search query.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<PlaceholderItem> mValues;
    private List<PlaceholderItem> mFilteredValues;
    /**
     * Constructs a new adapter with the given list of items.
     * @param items the list of items to display
     */
    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
        mFilteredValues = items;
    }
    /**
     * Filters the list of items based on the given query and updates the view.
     * @param query the search query to filter by
     */
    public void filter(String query) {
        List<PlaceholderItem> filteredList = new ArrayList<>();
        for (PlaceholderItem item : mValues) {
            if (item.content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mFilteredValues.clear();
        mFilteredValues.addAll(filteredList);
        notifyDataSetChanged();
    }
    /**
     * This method inflates the layout for the RecyclerView item and creates a new ViewHolder object.
     * @param parent the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     * @return a new ViewHolder object.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nearby_codes_list, parent, false);
        return new ViewHolder(view);
    }
    /**
     * This method sets the content of the RecyclerView item at the specified position.
     * @param holder the ViewHolder object for the item to be updated.
     * @param position the position of the item to be updated.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position < mFilteredValues.size()) {
            holder.mItem = mFilteredValues.get(position);
            holder.mContentView.setText(mFilteredValues.get(position).content);
            holder.mDistanceView.setText(mFilteredValues.get(position).details);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Implement item click listener
                }
            });
        }
    }
    /**
     * This method returns the number of items in the adapter's data set.
     * @return the number of items in the adapter's data set.
     */
    @Override
    public int getItemCount() {
        return mFilteredValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mDistanceView;
        public PlaceholderItem mItem;
        /**
         * This class defines the ViewHolder for the RecyclerView items.
         * @param view view to hold
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDistanceView = view.findViewById(R.id.distance_text);
            mContentView = view.findViewById(R.id.item_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

