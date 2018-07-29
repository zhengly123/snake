package controller;

import config.MapConfig;
import entity.Chess;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class GameController extends TimerTask {
    private boolean isRunning=false;
//    MapConfig mapConfig;
    private Chess chess;
    private int nPlayer;
    private int speed=5, clock;
    private int[] playerDirection;
    private boolean[] lose;
    Logger logger;
    private Timer timer;

    public void setPlayerDirection(int c, int direction) {
        playerDirection[c]=direction;
    }

    public GameController(MapConfig mapConfig) {
        this(new Chess(mapConfig));
    }

    private GameController(Chess chess) {
        this.chess = chess;
        nPlayer=chess.getMapConfig().nPlayer;
        playerDirection=new int[nPlayer];
        lose=new boolean[nPlayer];
        logger=Logger.getLogger("GameController");
    }

    public void start() {
        logger.info("Game Start");
        isRunning=true;
        clock =speed;
        timer=new Timer();


        chess.printMap();
        System.out.println("-------init above-------");
        timer.schedule(this,100,100);
    }

    public void pause() {
        isRunning=false;
    }

    public void stop() {
        isRunning=false;
    }

    @Override
    public void run() {
        if (!isRunning) return;
        if (clock > 0) {
            clock--;
            return;
        }
        lose=chess.move(playerDirection);
        for (int i = 0; i < nPlayer; ++i) {
            if (lose[i]) {
                playerLose(i);
                //2 players only
                isRunning=false;
            }
        }
        chess.printMap();
        System.out.print("-------------------\n");

        clock =speed;
    }

    private void playerLose(int c) {
        System.out.printf("%d lose\n",c);
    }
}


class PlayerMove {
    int direction;
}