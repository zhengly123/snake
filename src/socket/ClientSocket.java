package socket;

import Window.ClientLogin;
import Window.GameWindow;
import Window.RankingWindow;
import config.ClientMessage;
import config.MapConfig;
import config.ServerMessage;
import controller.KeyController;
import controller.ServerMainController;

import javax.swing.*;
import java.awt.color.CMMException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private GameWindow gameWindow;
    /**
     * 0 is NotConnected, 1 is connected, 2 is ready, 3 is playing
     */
    private int clientStatus;
    String[] usernames;
    int room;
    volatile boolean gameStopped=false;


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

    public ServerMessage getRanking() {
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setRankingAsk();
        ServerMessage serverMessage=null;
        try {
            oos.writeObject(clientMessage);
            serverMessage=(ServerMessage)ois.readObject();
        } catch (IOException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Cannot connect to server.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Unknown error.");
        }
        return serverMessage;
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

    /**
     * 接受初次信息，建立地图和其余数据。然后不断接受新的数据。
     */
    @Override
    public void run() {
        try {
            Object object= null;
            object = ois.readObject();
            ServerMessage serverMessage=(ServerMessage)object;
            assert (serverMessage.hasInit);
            logger.info("Receive init game information");
            //TODO: start a game logic
            showGameWindow(serverMessage.getMapConfig(),this.usernames=serverMessage.getUsernames());
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
                if (gameStopped)
                    break;
                Object object2 = ois.readObject();
                ServerMessage serverMessage=(ServerMessage)object2;
//                ois.reset();
                logger.info("recv server message");
                serverMessage.print();
                if (serverMessage.hasChess) {
                    gameWindow.paintChess(serverMessage.getChess());
                    gameWindow.updateUserList(serverMessage.getChess(),serverMessage.getPlayerOnline());
                    gameWindow.setStatusText("Running",0);
                    System.out.printf("-------Round %d-------",serverMessage.round);
                    serverMessage.getChess().printMap();
                    System.out.println("----------------------");
                }

                if (serverMessage.hasPause) {
                    pause(serverMessage.getPauseFrom());
                }

                if (serverMessage.hasMessage) {
                    gameWindow.addChatMessage(serverMessage.getMessageFrom(),
                            serverMessage.getMessage());
                }

                if (serverMessage.hasEnd) {
                    endGame(serverMessage);
                    RankingWindow rankingWindow = new RankingWindow(serverMessage.getRankingNames(),
                            serverMessage.getRankingPoints());
                    logger.info("Show Ranking list");
                    break;

                }
                //TODO:处理其余的消息
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.warning("ClientSocket finished");
    }

    private void endGame(ServerMessage serverMessage) {
        gameWindow.setItemEnabled(false);
        switch (serverMessage.getResult()) {
            case WIN:
                JOptionPane.showMessageDialog(null,"You are winner!");
                break;
            case TIE:
                JOptionPane.showMessageDialog(null,"It's a tie!");
                break;
            case LOSE:
                JOptionPane.showMessageDialog(null,"You lose it!");
                break;
        }

        try {
            oos.close();
            ois.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause(int pauseFrom) {
        gameWindow.setStatusText("Pause",pauseFrom);
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

    private void showGameWindow(MapConfig mapConfig,String[] usernames) {
        KeyController keyController=new controller.KeyController(this);
        JFrame frame = new JFrame("Snake game window");
        frame.setContentPane((gameWindow=new GameWindow(mapConfig,this,frame,usernames,
                keyController)).cp);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addKeyListener(keyController);
        frame.setSize(700, 600);
//        frame.setResizable(false);
        frame.setVisible(true);
        frame.requestFocus();
        //如果窗口关闭，则login Window恢复，释放线程
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                gameStopped=true;
                loginWindow.clearToInit();
            }
        });
    }


    public void sendPause() {
        ClientMessage clientMessage=new ClientMessage();
        clientMessage.setStop();
        try {
            oos.writeObject(clientMessage);
            logger.info("Send stop");
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Fail to Send stop");
        }
    }

    public void sendMessage(String msg) {
        ClientMessage clientMessage=new ClientMessage();
        clientMessage.setMessage(msg);
        try {
            oos.writeObject(clientMessage);
            logger.info("Send msg");
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Fail to Send msg");
        }
    }
}
