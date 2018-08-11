package controller;

import config.MapConfig;
import socket.ServerPeerSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServerMainController extends Thread{
    ServerSocket serverSocket;
    HashMap<Integer,GameController> gameControllerHashMap=new HashMap<>();
    private Logger logger;

    public ServerMainController() {
        logger=Logger.getLogger("ServerMain");
    }

    @Override
    public void run() {
        int port=8123;
        try {
            this.serverSocket = new ServerSocket(port);
            logger.info("server socket start, port "+port);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Cannot open server socket");
            return;
        }
        while (true) {
            try {
                Socket socket=serverSocket.accept();
                ServerPeerSocket sps = new ServerPeerSocket(socket, this);
                (new Thread(sps)).start();
                logger.info("server socket accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int createNewGame(Socket socket, ObjectOutputStream oos, ObjectInputStream ois,
                             MapConfig mapConfig, String username, int gameSpeed) {
        //TODO:user game Speed ????
        int room=gameControllerHashMap.size();
        gameControllerHashMap.put(room,new GameController(mapConfig));
        gameControllerHashMap.get(room).addUser(username,socket,oos,ois);
        return room;
    }

    public void joinGame(Socket socket, ObjectOutputStream oos, ObjectInputStream ois,
                         int room, String username)  {
        gameControllerHashMap.get(room).addUser(username,socket,oos,ois);
    }
}
