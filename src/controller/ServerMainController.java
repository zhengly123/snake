package controller;

import Window.ClientLogin;
import config.MapConfig;
import socket.ServerPeerSocket;

import javax.swing.*;
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

    public ServerMainController(ClientLogin loginWindow) {
        this.logginWindow = loginWindow;
        logger=Logger.getLogger("ServerMain");
    }

    private ClientLogin logginWindow;

    public HashMap<Integer, GameController> getGameControllerHashMap() {
        return gameControllerHashMap;
    }

    @Override
    public void run() {
        int port;
        try {
            port=logginWindow.getPort();
            this.serverSocket = new ServerSocket(port);
            logger.info("server socket start, port "+port);
            JOptionPane.showMessageDialog(null,
                    "Create server successfully");
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Cannot open server socket");
            logginWindow.clearToInit();
            JOptionPane.showMessageDialog(null,
                    "Cannot create server on this port.");
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
                             MapConfig mapConfig, String username, int gameSpeed,ServerPeerSocket sps) {
        //TODO:user game Speed ????
        int room=gameControllerHashMap.size();
        logger.info("New room number "+room);
        gameControllerHashMap.put(room,new GameController(mapConfig,room));
        gameControllerHashMap.get(room).addUser(username,socket,oos,ois,sps,room);
        return room;
    }

    public void joinGame(Socket socket, ObjectOutputStream oos, ObjectInputStream ois,
                         int room, String username,ServerPeerSocket sps)  {
        gameControllerHashMap.get(room).addUser(username, socket, oos, ois, sps,room);
    }
}
