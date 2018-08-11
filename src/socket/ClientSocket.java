package socket;

import Window.ClientLogin;
import Window.GameWindow;
import config.ClientMessage;
import config.MapConfig;
import config.ServerMessage;
import controller.ServerMainController;

import javax.swing.*;
import java.awt.color.CMMException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

//enum ClientStatus{NotConnected,connected,Ready};

public class ClientSocket implements Runnable{
    private Logger logger = Logger.getLogger("ClientSocket");
    private Socket socket=null;
    private ObjectOutputStream oos=null;
    private ObjectInputStream ois=null;
    private ClientLogin loginWindow;
    /**
     * 0 is NotConnected, 1 is connected, 2 is ready, 3 is playing
     */
    private int clientStatus;
    int room;


    public ClientSocket(ClientLogin loginWindow) {
        this.loginWindow = loginWindow;
        clientStatus=0;
    }

    public void connect(String ip, int port) {
        try {
            socket=new Socket(ip,port);
            System.out.println("INFO: client connected");
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            clientStatus=1;
            logger.info("connection established" );
        } catch (IOException e1) {
            e1.printStackTrace();
            logger.warning("cannot create socket");
            JOptionPane.showMessageDialog(null,"Cannot connect to server. " +
                    "Please check the server IP and port.");
            return;
        }
        loginWindow.changeButtonStatus(clientStatus);
    }

    public void createNewRoom(String username, MapConfig mapConfig,int gameSpeed) {
        ClientMessage clientMessage=new ClientMessage();
        clientMessage.setNewGame(username,mapConfig,gameSpeed);
        try {
            oos.writeObject(clientMessage);
            logger.info("Send create room request");
            Object object=ois.readObject();
            ServerMessage serverMessage=(ServerMessage)object;
            assert (serverMessage.hasRoom);
            room=serverMessage.getRoom();
            logger.info("Receive create room reply");
            loginWindow.showRoom(room);
            loginWindow.changeButtonStatus(clientStatus=2);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Cannot send msg to create new room");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加入房间，过程阻塞
     * @param username
     * @param room
     */
    public void joinRoom(String username, int room) {
        ClientMessage clientMessage=new ClientMessage();
        clientMessage.setJoinGame(username,room);
        try {
            oos.writeObject(clientMessage);
            logger.info("Send join msg");
            ServerMessage serverMessage=(ServerMessage) ois.readObject();
            assert (serverMessage.hasRoom && serverMessage.getRoom() == room);
            loginWindow.changeButtonStatus(clientStatus=2);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Object object= null;
            object = ois.readObject();
            ServerMessage serverMessage=(ServerMessage)object;
            assert serverMessage.hasInit;
            logger.info("Receive init game information");
            //TODO: start a game logic
            showGameWindow();
            //init do not have chess
//            serverMessage.getChess().printMap();
            System.out.println("----------------------");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Object object2 = ois.readObject();
                ServerMessage serverMessage=(ServerMessage)object2;
//                ois.reset();
                logger.info("recv server message");
                serverMessage.print();
                if (serverMessage.hasChess) {
                    System.out.printf("-------Round %d-------",serverMessage.round);
                    serverMessage.getChess().printMap();
                    System.out.println("----------------------");
                }
                //TODO:处理其余的消息
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendMove(int direction) {
        ClientMessage clientMessage=new ClientMessage();
        clientMessage.setDirection(direction);
        try {
            oos.writeObject(clientMessage);
            logger.info("Sended move msg");
        } catch (IOException e) {
//            e.printStackTrace();
            logger.info("Fail to send move msg");
        }
    }

    private void showGameWindow() {
        JFrame frame = new JFrame("Snake game window");
        frame.setContentPane(new GameWindow(this).cp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new controller.KeyController(this));
        frame.setSize(700, 600);
//        frame.setResizable(false);
        frame.setVisible(true);
    }
}
