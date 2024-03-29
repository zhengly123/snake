package socket;

import config.ClientMessage;
import config.ServerMessage;
import controller.GameController;
import controller.ServerMainController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 服务器在游戏开始前的设置、准备阶段，进行通讯。进入房间后，则释放。
 */
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
        while (true) {
            if (clientMessage.hasNewGame) {
                room=serverMainController.createNewGame(socket,oos,ois,clientMessage.getMapConfig(),clientMessage.getUsername(),
                        clientMessage.getGameSpeed(),this);
                break;
            } else if (clientMessage.hasJoinGame) {
                serverMainController.joinGame(socket,oos,ois,clientMessage.getRoom(),
                        clientMessage.getUsername(),this);
                break;
            }
            else if (clientMessage.hasRankingAsk){
                ArrayList<String> names=new ArrayList<>();
                ArrayList<Integer> points=new ArrayList<>();
                GameController.readRank(names,points);
                ServerMessage serverMessage=new ServerMessage();
                serverMessage.setRanking(names,points);
                try {
                    oos.writeObject(serverMessage);
                    logger.info("Sent ranking msg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                logger.severe("Recv unqualified 1st msg");
            }

            Object object= null;
            try {
                object = ois.readObject();
                clientMessage=(ClientMessage) object;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            logger.info("Client 1st msg recved again");
        }
        //send room number to client.
//        serverMessage.setInit(serverMainController.getGameControllerHashMap().get(room).getMapConfig(),
//                serverMessage.getUsernames());
//        serverMainController.getGameControllerHashMap().get(room).testFullPlayer();
    }

    public void sendRoomInfo(int room) {
        //最后一个人，game controller发送的信息会先到达。所以gc发送两次，覆盖掉这个信息
        ServerMessage serverMessage=new ServerMessage();
        serverMessage.setRoom(room);
        try {
            oos.writeObject(serverMessage);
            logger.info("Send room number to client, room num is "+room);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Fail to send room number to client");
        }
    }
}
