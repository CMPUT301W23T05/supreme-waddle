package com.example.qrky;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CommunityAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<List<String>> playerAndScoreRanked = new ArrayList<>();

    public CommunityAdapter(Context context, HashMap<String, String> playerAndScore) {
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String key: playerAndScore.keySet()) {
            List<String> aPlayerAndScore = Arrays.asList(key, playerAndScore.get(key));
            playerAndScoreRanked.add(aPlayerAndScore);
        }
        Log.i("CommunityAdapter", "playerAndScore unsorted: " + playerAndScoreRanked);

        // sort playerAndScoreRanked by score (descending) without using lambda expressions
        try {
            for (int i = 0; i < playerAndScoreRanked.size(); i++) {
                for (int j = i + 1; j < playerAndScoreRanked.size(); j++) {
                    if (Integer.parseInt(playerAndScoreRanked.get(i).get(1)) < Integer.parseInt(playerAndScoreRanked.get(j).get(1))) {
                        List<String> temp = playerAndScoreRanked.get(i);
                        playerAndScoreRanked.set(i, playerAndScoreRanked.get(j));
                        playerAndScoreRanked.set(j, temp);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
        if (convertView == null) {convertView = inflater.inflate(R.layout.a_player_brief, null);}

        TextView username = convertView.findViewById(R.id.username);
        TextView score = convertView.findViewById(R.id.score);

        username.setText(playerAndScoreRanked.get(position).get(0));
        score.setText(playerAndScoreRanked.get(position).get(1));

        return convertView;
    }

    @Override
    public int getCount() {
        if (playerAndScoreRanked == null) {return 0;}
        return playerAndScoreRanked.size();
    }
    @Override
    public Object getItem(int position) {
        return playerAndScoreRanked.get(position);
    }
    @Override
    public long getItemId(int position) {
        return (long) Integer.parseInt(playerAndScoreRanked.get(position).get(1));
    }
}
