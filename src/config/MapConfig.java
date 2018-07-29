package config;

public class MapConfig {
    public int getSize() {
        return size;
    }

    public final int size,nPlayer,totalSize;
    private final int nWalls,nSnakes,nHoles,nEggs;
    private int snakeInitLen;
    private int holeLen;

//    private String background;

    public MapConfig() {
        nPlayer=2;
        size=10;
        nWalls=2;
        nSnakes=2;
        nHoles=2;
        nEggs=2;
        snakeInitLen=2;
        totalSize=size+nHoles/2;
        holeLen=2;
    }

    public int getSnakeInitLen() {
        return snakeInitLen;
    }

    public int getnWalls() {
        return nWalls;
    }

    public int getnSnakes() {
        return nSnakes;
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
