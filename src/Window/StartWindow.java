package Window;

import javax.swing.*;

public class StartWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setContentPane(new LoginWindow().cp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(700, 600);
        frame.pack();
//        frame.setResizable(false);
        frame.setVisible(true);
    }
}
