package com.lifegame.gameoflifejfx;

public class Cell {
    int numOfNeighbors;
    boolean lifeState;  // false = dead, true = alive

    short[] rgb;       // stores the cell's color DNA --- WIP feature

    // creates a new cell object, with the default state of being dead
    public Cell() {
        numOfNeighbors = 0;
        lifeState = false;
        rgb = new short[3];
    }

    public void setNumOfNeighbors(int num) {
        numOfNeighbors = num;
    }

    public void setLifeState(boolean state) {
        lifeState = state;
    }

    public boolean getLifeState() {
        return lifeState;
    }

    public int getNumOfNeighbors() {
        return numOfNeighbors;
    }

    public short[] getRGB() {
        return rgb;
    }

    public void setRGB(short[] newRGB) {
        rgb = newRGB;
    }
}
