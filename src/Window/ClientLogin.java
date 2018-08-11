package Window;

import config.MapConfig;
import controller.ServerMainController;
import socket.ClientSocket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientLogin {
    public JPanel cp;
    private JButton startServerButton;
    private JButton connectToServerButton;
    private JButton newRoomButton;
    private JButton joinRoomButton;
    private JTextField a127001TextField;
    private JTextField a8123TextField;
    private JTextField a0TextField;
    private JTextField aliceTextField;
    private JRadioButton defaultModeRadioButton;
    private JRadioButton customedModeRadioButton;
    private JSlider gameSpeedSlider;
    private JSlider playerNumberslider;
    private Logger logger=Logger.getLogger("Login");

    ClientSocket clientSocket;
    ServerMainController serverMainController;
    public ClientLogin() {
        connectToServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: connect to the server
                clientSocket=new ClientSocket(ClientLogin.this);
                clientSocket.connect(a127001TextField.getText(),Integer.parseInt(a8123TextField.getText()));
            }
        });
        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverMainController=new ServerMainController();
                ClientLogin.this.changeButtonStatus(3);
                serverMainController.start();
            }
        });
        newRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientSocket.createNewRoom(aliceTextField.getText(),getMapConfig(),gameSpeedSlider.getValue());
            }
        });
        joinRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientSocket.joinRoom(aliceTextField.getText(),Integer.parseInt(a0TextField.getText()));
            }
        });
    }

    private MapConfig getMapConfig() {
        return new MapConfig(playerNumberslider.getValue(),gameSpeedSlider.getValue());
    }

    private void changeAllButtonStatus(boolean startServer, boolean connectToServer, boolean newRoom,
                                       boolean joinRoom) {
        startServerButton.setEnabled(startServer);
        connectToServerButton.setEnabled(connectToServer);
        newRoomButton.setEnabled(newRoom);
        joinRoomButton.setEnabled(joinRoom);
    }

    /**
     * 修改全部按钮的Enabled
     * @param clientStatus 0 is NotConnected, 1 is connected, 2 is ready, 3 is playing
     */
    public void changeButtonStatus(int clientStatus) {
        switch (clientStatus) {
            case 0:
                changeAllButtonStatus(true,true,false,false);
                break;
            case 1:
                changeAllButtonStatus(false,false,true,true);
                break;
            case 2:
                changeAllButtonStatus(false,false,false,false);
                JOptionPane.showMessageDialog(null, "Waiting for other players");
                break;
            case 3:
                changeAllButtonStatus(false,false,false,false);
                break;
        }
    }

    public void showRoom(Integer room) {
        a0TextField.setText(room.toString());
        JOptionPane.showMessageDialog(null, "Your room number is "+room+".\n" +
                "Please wait for other players.");
        logger.info("Get room number "+room);
    }
}
