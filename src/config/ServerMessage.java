package config;

import entity.Chess;
import entity.Point;

import java.io.Serializable;
import java.util.HashMap;


public class ServerMessage implements Serializable{
    public boolean hasInit,hasChess,hasStatus,hasMessage,hasRoom,hasCollisions, hasPause;
    MapConfig mapConfig;
    Chess chess;

    String message,messageFrom;

    Point[] collisions;
    String[] usernames;
    Result result;
    public int round;

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

    public void setPause() {
        hasPause =true;
    }

    public void setMessage(String username,String message) {
        hasMessage=true;
        messageFrom=username;
        this.message = message;
    }

    public void setEnd(Result result) {
//        this.collisions = collisions;
        this.result=result;
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

    public void setChess(Chess chess,int round) {
        hasChess=true;
        this.chess = chess;
        this.round=round;
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
