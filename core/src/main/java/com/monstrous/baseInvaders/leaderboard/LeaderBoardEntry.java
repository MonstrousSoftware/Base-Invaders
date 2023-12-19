package com.monstrous.baseInvaders.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

public class LeaderBoardEntry {
    public String rank;
    public String score;
    public long sort;
    public String extra_data;
    public String user;
    public String userId;
    public String displayName;
    public String stored;
    public long storedTimestamp;    // seconds since 1950?

    protected static LeaderBoardEntry fromJson(JsonValue json, int rank) {
        LeaderBoardEntry entry = new LeaderBoardEntry();
        entry.rank = String.valueOf(rank);
        entry.score = json.getString("score");
        entry.sort = json.getLong("sort");
        entry.extra_data = json.getString("extra_data");
        String userId = json.getString("user_id");

        if (userId != null && !userId.isEmpty()) {
            entry.userId = userId;
            entry.displayName = json.getString("user");
            //entry.currentPlayer = (currentPlayer != null && currentPlayer.equalsIgnoreCase(gje.displayName));
        } else
            entry.displayName = json.getString("guest");

        entry.stored = json.getString("stored");
        entry.storedTimestamp = json.getLong("stored_timestamp");

        return entry;
    }

    public void print() {
        Gdx.app.log("Score entry "+rank, displayName + " "+score + " "+stored);
    }

}
