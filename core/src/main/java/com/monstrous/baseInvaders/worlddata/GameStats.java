package com.monstrous.baseInvaders.worlddata;

public class GameStats {
    public float gameTime;
    public int numFlags;
    public int techCollected;
    public int numEnemies;
    public boolean levelComplete;
    public int ufosSpawned;
    public int speed;

    public GameStats() {
        reset();
    }

    public void reset() {
        gameTime = 0;
        numFlags = 0;
        techCollected = 0;
        numEnemies = 0;
        ufosSpawned = 0;
        levelComplete = false;
        speed = 0;
    }
}
