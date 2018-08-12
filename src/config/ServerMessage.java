package config;

import entity.Chess;
import entity.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class ServerMessage implements Serializable{
    public boolean hasInit,hasChess,hasStatus,hasMessage,hasRoom,hasCollisions, hasPause,hasRanking,hasEnd;
    MapConfig mapConfig;
    Chess chess;
    boolean[] playerOnline;
    int pauseFrom;

    String message,messageFrom;

    Point[] collisions;
    String[] usernames;
    Result result;

    ArrayList<String> rankingNames;
    ArrayList<Integer> rankingPoints;
    public int round;

    public ArrayList<String> getRankingNames() {
        return rankingNames;
    }

    public void setRanking(ArrayList<String> rankingNames,ArrayList<Integer> rankingPoints) {
        hasRanking=true;
        this.rankingNames = rankingNames;
        this.rankingPoints = rankingPoints;
    }

    public ArrayList<Integer> getRankingPoints() {
        return rankingPoints;
    }

    public boolean[] getPlayerOnline() {
        return playerOnline;
    }

    public int getPauseFrom() {
        return pauseFrom;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        hasRoom=true;
        this.room = room;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    int room;

    public void setInit(MapConfig mapConfig,String[] usernames) {
        hasInit=true;
        this.mapConfig = mapConfig;
        this.usernames=usernames;
    }

    public String[] getUsernames() {
        return usernames;
    }

    public void setPause(int index) {
        hasPause =true;
        pauseFrom =index;
    }

    public void setMessage(String username,String message) {
        hasMessage=true;
        messageFrom=username;
        this.message = message;
    }

    public void setEnd(Result result) {
        hasEnd=true;
//        this.collisions = collisions;
        this.result=result;
    }

    public Result getResult() {
        return result;
    }

    public void print() {
        System.out.printf("Init Chess Status Msg Room Collisions\n");
        System.out.printf("%b  %b %b  %b %b  %b\n",hasInit,hasChess,hasStatus,
                hasMessage,hasRoom,hasCollisions);
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public String getMessage() {
        return message;
    }

    public Point[] getCollisions() {
        return collisions;
    }

    public Chess getChess() {
        return chess;
    }

    public void setChess(Chess chess,int round,boolean[] playerOnline) {
        hasChess=true;
        this.chess = chess;
        this.round=round;
        this.playerOnline=playerOnline;
    }

    public static boolean isSerializable(final Object candidateClass)
    {
        return candidateClass instanceof Serializable;
    }

    public static void main(String[] args) {
        ServerMessage serverMessage=new ServerMessage();
        serverMessage.setRoom(1);
        MapConfig mapConfig=new MapConfig();
        System.out.println(isSerializable(serverMessage));
        System.out.println(isSerializable(serverMessage.getChess()));
        System.out.println(isSerializable(serverMessage.getMapConfig()));
        System.out.println(isSerializable(mapConfig));
    }
}
