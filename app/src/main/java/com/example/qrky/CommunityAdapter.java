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

/**
 * CommunityAdapter.java
 * Adapter for RecyclerView of leaderboard in CommunityFragment. This adapter is used to
 * display the leaderboard in the CommunityFragment. It is used to display all the players,
 * ordered by their score. It is also used to display the players that match the search query.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/03/12
 * @see CommunityFragment
 */
public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<List<String>> playerAndScoreRanked;

        /**
         * Constructor for CommunityAdapter.
         *
         * @param playerAndScore: HashMap of player and score {username: score}
         * @since 1.0
         */
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

        /**
         * Gets the view holder for the RecyclerView.
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         *
         * @param viewType The view type of the new View as an integer.
         * @return A new CommunityViewHolder that uses a View of a_player_brief.
         * @since 1.0
         */
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


        /**
         * View holder for RecyclerView of leaderboard in CommunityFragment.java
         *
         * @author Franco Bonilla
         * @version 1.0 2023/03/12
         * @see CommunityAdapter
         */
        public static class CommunityViewHolder extends RecyclerView.ViewHolder {

            private TextView usernameView;
            private TextView scoreView;

            public CommunityViewHolder(@NonNull View itemView) {
                super(itemView);
                usernameView = itemView.findViewById(R.id.username);
                scoreView = itemView.findViewById(R.id.score);
            }
        }

        /**
         * Gets the number of items in the leaderboard (playerAndScoreRanked).
         *
         * @return The number of items in the RecyclerView.
         * @since 1.0
         */
        @Override
        public int getItemCount() {
            return playerAndScoreRanked.size();
        }

        /**
         * Updates the leaderboard with the new HashMap of player and score.
         *
         * @param playerAndScore The new HashMap of player and score.
         * @since 1.0
         */
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
