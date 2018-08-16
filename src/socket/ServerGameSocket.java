package socket;

import config.ClientMessage;
import config.ServerMessage;
import controller.GameController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * 服务器在游戏进行的过程中，为每个玩家创建一个socket进行通讯。
 */
public class ServerGameSocket implements Runnable {
    ObjectInputStream ois;
    GameController gameController;
    int playerIndex;

    public ServerGameSocket(ObjectInputStream ois, GameController gameController, int playerIndex) {
        this.ois = ois;
        this.gameController = gameController;
        this.playerIndex = playerIndex;
    }

    /**
     * 阻塞等待新的数据，并选择对应函数进行处理
     */
    @Override
    public void run() {
        while (true) {
            try {
                ClientMessage clientMessage =(ClientMessage) ois.readObject();
                if (clientMessage.hasDirection) {
                    gameController.setPlayerDirection(playerIndex,
                            clientMessage.getDirection());
                }
                if (clientMessage.hasStop) {
                    gameController.pause(playerIndex);
                }
                if (clientMessage.hasMessage) {
                    gameController.sendMessage(playerIndex,clientMessage.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
                gameController.playerOffline(playerIndex);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
