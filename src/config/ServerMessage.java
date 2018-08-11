package config;

import entity.Chess;
import entity.Point;
import entity.Snake;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class ServerMessage implements Serializable{
    public boolean hasInit,hasChess,hasStatus,hasMessage,hasRoom,hasCollisions;
    MapConfig mapConfig;
    Chess chess;

    HashMap<String,String> message;

    Point[] collisions;
    Result result;
    public int round;

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        hasRoom=true;
        this.room = room;
    }

    int room;

    public void setInit(MapConfig mapConfig) {
        hasInit=true;
        this.mapConfig = mapConfig;
    }

    public void setMessage(HashMap<String, String> message) {
        hasMessage=true;
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

    public HashMap<String, String> getMessage() {
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
