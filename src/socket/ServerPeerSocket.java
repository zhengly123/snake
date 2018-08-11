package socket;

import config.ClientMessage;
import config.ServerMessage;
import controller.GameController;
import controller.ServerMainController;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

public class ServerPeerSocket implements Runnable{
    Socket socket;
    int room;
    Logger logger=Logger.getLogger("ServerPeerSocket");
    ServerMainController serverMainController;
    ObjectOutputStream oos;

    public ServerPeerSocket(Socket socket, ServerMainController serverMainController) {
        this.socket = socket;
        this.serverMainController = serverMainController;
    }

    @Override
    public void run() {
        logger.info("Start to run");
        InputStream inputStream= null;
        ObjectInputStream ois;
        ClientMessage clientMessage=null;
        try {
            inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            oos.reset();
            ois = new ObjectInputStream(bufferedInputStream);
            logger.info("oos/ois create successfully");
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Cannot create oos/ois");
            return;
        }
        logger.info("wait for first msg");

        while (true) {
            try {
                Object object=ois.readObject();
                clientMessage=(ClientMessage) object;
                logger.info("Client 1st msg recved");
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
        if (clientMessage.hasNewGame) {
            room=serverMainController.createNewGame(socket,oos,ois,clientMessage.getMapConfig(),clientMessage.getUsername(),
                    clientMessage.getGameSpeed(),this);
        } else if (clientMessage.hasJoinGame) {
            serverMainController.joinGame(socket,oos,ois,clientMessage.getRoom(),
                    clientMessage.getUsername(),this);
        }
        else
        {
            logger.severe("Recv unqualified 1st msg");
            return;
        }
        //send room number to client.
//        serverMessage.setInit(serverMainController.getGameControllerHashMap().get(room).getMapConfig(),
//                serverMessage.getUsernames());
//        serverMainController.getGameControllerHashMap().get(room).testFullPlayer();
    }

    public void sendRoomInfo() {
        //最后一个人，game controller发送的信息会先到达。所以gc发送两次，覆盖掉这个信息
        ServerMessage serverMessage=new ServerMessage();
        serverMessage.setRoom(room);
        try {
            oos.writeObject(serverMessage);
            logger.info("Send room number to client");
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Fail to send room number to client");
        }
    }
}
