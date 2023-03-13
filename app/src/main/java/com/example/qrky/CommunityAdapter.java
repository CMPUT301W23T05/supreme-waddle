package com.example.qrky;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<List<String>> playerAndScoreRanked;

        public CommunityAdapter(HashMap<String, String> playerAndScore) {
            List<List<String>> playerAndScoreMessy = new ArrayList<>();
            for (String key: playerAndScore.keySet()) {
                List<String> aPlayerAndScore = Arrays.asList(key, playerAndScore.get(key));
                playerAndScoreMessy.add(aPlayerAndScore);
            }
            Log.i("CommunityAdapter", "playerAndScore unsorted: " + playerAndScoreMessy);

            // sort playerAndScoreRanked by score (descending) without using lambda expressions
            for (int i = 0; i < playerAndScoreMessy.size(); i++) {
                for (int j = i + 1; j < playerAndScoreMessy.size(); j++) {
                    if (Integer.parseInt(playerAndScoreMessy.get(i).get(1)) < Integer.parseInt(playerAndScoreMessy.get(j).get(1))) {
                        List<String> temp = playerAndScoreMessy.get(i);
                        playerAndScoreMessy.set(i, playerAndScoreMessy.get(j));
                        playerAndScoreMessy.set(j, temp);
                    }
                }
            }
            this.playerAndScoreRanked = playerAndScoreMessy;
            Log.i("CommunityAdapter", "playerAndScore sorted: " + playerAndScoreRanked);
        }

        @NonNull
        @Override
        public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_player_brief, parent, false);
            return new CommunityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
            holder.usernameView.setText((position+1)+". "+playerAndScoreRanked.get(position).get(0));
            holder.scoreView.setText(playerAndScoreRanked.get(position).get(1));
        }

        public static class CommunityViewHolder extends RecyclerView.ViewHolder {

            private TextView usernameView;
            private TextView scoreView;

            public CommunityViewHolder(@NonNull View itemView) {
                super(itemView);
                usernameView = itemView.findViewById(R.id.username);
                scoreView = itemView.findViewById(R.id.score);
            }
        }

        @Override
        public int getItemCount() {
            return playerAndScoreRanked.size();
        }

        // update data if their is a change
        public void update(HashMap<String, String> playerAndScore) {
            playerAndScoreRanked.clear();
            List<List<String>> playerAndScoreMessy = new ArrayList<>();
            for (String key: playerAndScore.keySet()) {
                List<String> aPlayerAndScore = Arrays.asList(key, playerAndScore.get(key));
                playerAndScoreMessy.add(aPlayerAndScore);
            }

            // sort playerAndScoreRanked by score (descending) without using lambda expressions
            for (int i = 0; i < playerAndScoreMessy.size(); i++) {
                for (int j = i + 1; j < playerAndScoreMessy.size(); j++) {
                    if (Integer.parseInt(playerAndScoreMessy.get(i).get(1)) < Integer.parseInt(playerAndScoreMessy.get(j).get(1))) {
                        List<String> temp = playerAndScoreMessy.get(i);
                        playerAndScoreMessy.set(i, playerAndScoreMessy.get(j));
                        playerAndScoreMessy.set(j, temp);
                    }
                }
            }
            this.playerAndScoreRanked = playerAndScoreMessy;
            notifyDataSetChanged();
        }
}
