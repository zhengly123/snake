package main;

import config.MapConfig;
import controller.GameController;
import controller.KeyController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HotkeyDebug {
    public JPanel cp;
    private JButton button1;


    public HotkeyDebug() {
    }
}

class HotkeyTestGui {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ColorWorld");
        frame.setContentPane(new HotkeyDebug().cp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setSize(400,400);
        frame.setVisible(true);
        GameController gameController=new GameController(new MapConfig());
        frame.addKeyListener(new controller.KeyController(gameController));
    }
}