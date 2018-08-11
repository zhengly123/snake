package controller;

import socket.ClientSocket;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyController extends KeyAdapter {
    private GameController gameController=null;

    public KeyController(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private ClientSocket clientSocket=null;
//    public KeyController(GameController gameController) {
//        this.gameController = gameController;
//    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                System.out.println("Press UP");
//                gameController.setPlayerDirection(0,2);
                clientSocket.sendMove(2);
                break;
            case KeyEvent.VK_DOWN:
                System.out.println("Press DOWN");
//                gameController.setPlayerDirection(0,0);
                clientSocket.sendMove(0);
                break;
            case KeyEvent.VK_LEFT:
                System.out.println("Press LEFT");
//                gameController.setPlayerDirection(0,3);
                clientSocket.sendMove(3);
                break;
            case KeyEvent.VK_RIGHT:
                System.out.println("Press RIGHT");
//                gameController.setPlayerDirection(0,1);
                clientSocket.sendMove(1);
                break;
            case KeyEvent.VK_SPACE:
                System.out.println("Press SPACE");
//                gameController.start();
                clientSocket.sendPause();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {

    }
}
