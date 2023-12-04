package com.monstrous.transamtest.worlddata;

public class GameStats {
    public float gameTime;
    public int numFlags;
    public int flagsCollected;
    public int numEnemies;
    public boolean levelComplete;

    public GameStats() {
        reset();
    }

    public void reset() {
        gameTime = 0;
        numFlags = 0;
        flagsCollected = 0;
        numEnemies = 0;
        levelComplete = false;
    }
}
