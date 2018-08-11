package socket;

import config.ClientMessage;
import config.ServerMessage;
import controller.GameController;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerGameSocket implements Runnable {
    ObjectInputStream ois;
    GameController gameController;
    int playerIndex;

    public ServerGameSocket(ObjectInputStream ois, GameController gameController, int playerIndex) {
        this.ois = ois;
        this.gameController = gameController;
        this.playerIndex = playerIndex;
    }

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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
