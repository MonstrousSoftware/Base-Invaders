package com.monstrous.baseInvaders.worlddata;

public class GameStats {
    public float gameTime;
    public int techCollected;
    public boolean gameCompleted;
    public boolean scoreSavedToServer;
    public int ufosSpawned;
    public int speed;
    public int itemsRendered;
    private char[] timeStr = new char[8];


    public GameStats() {
        reset();
    }

    public void reset() {
        gameTime = 0;
        techCollected = 0;
        ufosSpawned = 0;
        gameCompleted = false;
        scoreSavedToServer = false;
        speed = 0;
        itemsRendered = 0;
    }

    public String getTimeString(){
        int time = (int)gameTime;
        int hr = time / 3600;
        int min = (time -3600*hr) / 60;
        int sec = time - 60*min - 3600*hr;
        timeStr[0] = (char) ('0'+ hr /10);
        timeStr[1] = (char) ('0'+ hr %10);
        timeStr[2] = ':';
        timeStr[3] = (char) ('0'+ min /10);
        timeStr[4] = (char) ('0'+ min %10);
        timeStr[5] = ':';
        timeStr[6] = (char) ('0'+ sec /10);
        timeStr[7] = (char) ('0'+ sec %10);
        return String.valueOf(timeStr);
    }
}
