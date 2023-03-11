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


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<PlaceholderItem> mValues;
    private List<PlaceholderItem> mFilteredValues;

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
        mFilteredValues = items;
    }

    public void filter(String query) {
        List<PlaceholderItem> filteredList = new ArrayList<>();
        for (PlaceholderItem item : mValues) {
            if (item.content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mFilteredValues = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nearby_codes_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return mFilteredValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mDistanceView;
        public PlaceholderItem mItem;

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
