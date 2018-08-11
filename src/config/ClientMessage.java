package config;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    public boolean hasNewGame,hasJoinGame,hasNewMapConfig,hasDirection,hasMessage,hasStop;

    //TODO:获得房间信息
    MapConfig mapConfig;
    String username;
    int gameSpeed;

    int room;

    int direction;
    String message;

//    boolean stop;

    public void setStop() {
        hasStop=true;
    }


    public void setDirection(int direction) {
        hasDirection=true;
        this.direction = direction;
    }


    public void setNewGame(String username, MapConfig mapConfig, int gameSpeed) {
        hasNewGame=true;
        this.username=username;
        this.mapConfig = mapConfig;
        this.gameSpeed=gameSpeed;
    }

    public void setMessage(String message) {
        hasMessage=true;
        this.message = message;
    }

    public void setJoinGame(String username, int room) {
        hasJoinGame=true;
        this.username=username;
        this.room=room;
//        this.gameSpeed=gameSpeed;
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public String getUsername() {
        return username;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public int getRoom() {
        return room;
    }

    public int getDirection() {
        return direction;
    }

    public String getMessage() {
        return message;
    }
}
