package config;

import java.io.Serializable;

public class MapConfig implements Serializable {
    public int getSize() {
        return size;
    }

    public final int size,nPlayer,totalSize;
    private final int nWalls, nLives,nHoles,nEggs;
    private int snakeInitLen;
    private int holeLen;

    public MapConfig(int nPlayer, int speed) {
        size=10;
        nWalls=2;
        nLives =2;
        nHoles=2;
        nEggs=2;
        snakeInitLen=2;
        totalSize=size+nHoles/2;
        holeLen=2;
        this.nPlayer = nPlayer;
        this.speed = speed;
    }

    private int speed;

//    private String background;

    public MapConfig() {
        nPlayer=2;
        size=10;
        nWalls=2;
        nLives =2;
        nHoles=2;
        nEggs=2;
        snakeInitLen=2;
        totalSize=size+nHoles/2;
        holeLen=2;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSnakeInitLen() {
        return snakeInitLen;
    }

    public int getnWalls() {
        return nWalls;
    }

    public int getnLives() {
        return nLives;
    }

    public int getnHoles() {
        return nHoles;
    }

    public int getnEggs() {
        return nEggs;
    }

    public int getHoleLen() {
        return holeLen;
    }
}
