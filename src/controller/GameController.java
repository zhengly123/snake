package controller;

import config.MapConfig;
import config.Result;
import config.ServerMessage;
import entity.Chess;
import socket.ServerGameSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

enum GameStatus{STOPPED,RUNNING, PAUSE,OFFLINE};

public class GameController extends TimerTask {
//    private boolean isRunning=false;
    GameStatus status;
//    MapConfig mapConfig;
    private Chess chess;
    private int nPlayer;
    private int speed=5, clock;
    private int[] playerDirection;
//    private boolean[] lose;
    /**
     * 多条生命累计的积分，在死亡或结束时累加
     */
    private int[] points;
    private int[] loseTime;
    private Logger logger;
    private Timer timer;
    private String[] userNames;
    private Socket[] sockets;
    private ObjectOutputStream[] objectOutputStreams;
    private ObjectInputStream[] objectInputStreams;
    private int nAddedPlayer;
    private int round=0, nLosePlayer =0;
    Thread[] gameThread;

    public void setPlayerDirection(Integer c, Integer direction) {
        playerDirection[c]=direction;
        logger.info("Player "+c+" " +userNames[c]+" direction "+direction);
    }

    public GameController(MapConfig mapConfig) {
        this(new Chess(mapConfig));
    }

    public synchronized void addUser(String userName,Socket socket,ObjectOutputStream oos,ObjectInputStream ois) {
        userNames[nAddedPlayer]=userName;
        sockets[nAddedPlayer]=socket;
        objectOutputStreams[nAddedPlayer]=oos;
        objectInputStreams[nAddedPlayer]=ois;
        nAddedPlayer++;
        logger.info("user "+userName+" added to the game. #currrent player "+nAddedPlayer);
        if (nAddedPlayer == nPlayer) {
            start();
        }
    }

    private GameController(Chess chess) {
        this.chess = chess;
        nPlayer=chess.getMapConfig().nPlayer;
        playerDirection=new int[nPlayer];
        for (int i = 0; i < nPlayer; ++i) {
            playerDirection[i]=chess.getSnakes(i).headDirection;
        }

//        lose=new boolean[nPlayer];
        userNames = new String[nPlayer];
        sockets = new Socket[nPlayer];
        points = new int[nPlayer];
        loseTime = new int[nPlayer];
        objectOutputStreams = new ObjectOutputStream[nPlayer];
        objectInputStreams = new ObjectInputStream[nPlayer];
        gameThread=new Thread[nPlayer];
        logger=Logger.getLogger("GameController");
    }

    /**
     * 在第一次发送中，提供mapConfig信息。之后的发送提供只提供chess
     */
    public void start() {
        logger.info("Game Start");
//        isRunning=true;
        status=GameStatus.RUNNING;
        clock =speed;
        ServerMessage serverMessage=new ServerMessage();
//        serverMessage.setInit();
        serverMessage.setInit(chess.getMapConfig());
        serverMessage.setChess(chess,round);
        for (Integer i=0;i<nPlayer;++i) {
            ObjectOutputStream oos = objectOutputStreams[i];
            try {
                oos.writeObject(serverMessage);
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Failed to Send chess to "+i);
            }
        }
        timer=new Timer();

        for (int i = 0; i < nPlayer; ++i) {
            gameThread[i]=new Thread(new ServerGameSocket(objectInputStreams[i],
                    this,i));
            gameThread[i].start();
        }
        chess.printMap();
        System.out.println("-------init above-------");
        timer.schedule(this,100,100);
        //TODO: test this game start
    }

    public void pause() {
//        isRunning=false;
        status=GameStatus.PAUSE;
    }

    public void stop() {
//        isRunning=false;
        status=GameStatus.STOPPED;
    }

    @Override
    public void run() {
//        if (!isRunning) return;
        if (status!=GameStatus.RUNNING) return;
        if (clock > 0) {
            clock--;
            return;
        }
        round++;
        boolean[] lose=chess.move(playerDirection);
        ServerMessage serverMessage=new ServerMessage();
        serverMessage.setChess(chess,round);
        //TODO: add collisions
        for (int i = 0; i < nPlayer; ++i) {
            if (lose[i]) {
                playerLose(i);
            }
            //Produce server message
            try {
                objectOutputStreams[i].writeObject(serverMessage);
                objectOutputStreams[i].flush();
                objectOutputStreams[i].reset();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Fail to send chess");
            }
        }
        System.out.printf("-------Round %d------\n",round);
        chess.printMap();
        System.out.print("------msg get chess---\n");
        serverMessage.getChess().printMap();
        System.out.print("----------------------\n");

        if (nLosePlayer >= nPlayer - 1) {
            endGame();
        }

        clock =speed;
    }

    private void playerLose(Integer c) {
        logger.info("Player "+c+" lose");
        loseTime[c]=round;
        nLosePlayer++;
//        serverMessage.setEnd(, Result.LOSE);
    }

    private void endGame() {
        ServerMessage serverMessage=new ServerMessage();
        //TODO: add game end operation
        for (int i = 0; i < nPlayer; ++i) {
            if (loseTime[i]==0||(nLosePlayer==nPlayer&&loseTime[i]==round))
                serverMessage.setEnd(Result.WIN);
            else
                serverMessage.setEnd(Result.LOSE);
            try {
                objectOutputStreams[i].writeObject(serverMessage);
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("Fail to send end game msg");
            }
        }
    }
}


class PlayerMove {
    int direction;
}