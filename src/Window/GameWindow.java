package Window;

import config.MapConfig;
import entity.Chess;
import entity.Snake;
import entity.Point;
import socket.ClientSocket;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class GameWindow {
    public JPanel cp;
    private JPanel chessPanel;
    //    private JTextPane chatPane;
    private JLabel statusJLabel;
    private JButton pauseButton;
    private JTextField sendMessageJTextField;
    private JTextArea chatListPane;
    private JButton playDefaultMusicButton;
    private JButton stopButton;
    private JButton localMusicButton;
    private JLabel musicNameLabel;
    private JTable userTable;
    //    private JScrollPane userList;
    //    private JTextArea sendMessageTextArea;
    MapConfig mapConfig;
    ClientSocket clientSocket;
    JPanel[][] chessArray;
    private JFrame outerFrame;

    public GameWindow(MapConfig mapConfig, ClientSocket clientSocket, JFrame outerFrame, String[] usernames) {
        this.mapConfig = mapConfig;
        this.clientSocket = clientSocket;
        this.outerFrame = outerFrame;

        GridLayout gridLayout = new GridLayout(mapConfig.size, mapConfig.size);
        //create UserList
        userTable = createUserTable(usernames);
        $$$setupUI$$$();

        chessPanel.setLayout(gridLayout);
        chessArray = new JPanel[mapConfig.size][];
        for (int i = 0; i < mapConfig.size; ++i) {
            chessArray[i] = new JPanel[mapConfig.size];
            for (int j = 0; j < mapConfig.size; ++j) {
                chessArray[i][j] = new JPanel();
                chessPanel.add(chessArray[i][j]);
            }
        }

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientSocket.sendPause();
            }
        });

        sendMessageJTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Type somekey");
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    super.keyTyped(e);
                    return;
                }
                clientSocket.sendMessage(sendMessageJTextField.getText());
                sendMessageJTextField.setText("");
//                sendMessageJTextField.transferFocus();
                outerFrame.requestFocus();
            }
        });

//        outerFrame.requestFocus();
        playDefaultMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        localMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFile();
                if (filepath != null) {
                    try {
                        play(Paths.get(filepath).toUri().toURL());
                        musicNameLabel.setText(Paths.get(filepath).getFileName().toString());
                        stopButton.setEnabled(true);
                        playDefaultMusicButton.setEnabled(false);
                        localMusicButton.setEnabled(false);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        playDefaultMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URL autumnUrl = this.getClass().getResource("/autumn.wav");
                musicNameLabel.setText("Autumn (Default)");
                play(autumnUrl);
                stopButton.setEnabled(true);
                playDefaultMusicButton.setEnabled(false);
                localMusicButton.setEnabled(false);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMusic();
                stopButton.setEnabled(false);
                playDefaultMusicButton.setEnabled(true);
                localMusicButton.setEnabled(true);
            }
        });
    }

    public void updateUserList(Chess chess) {
        for (int i = 0; i < mapConfig.nPlayer; ++i) {
            userTable.setValueAt(chess.getLives()[i], i, 2);
            userTable.setValueAt(chess.getPoints()[i], i, 3);
        }
    }

    public void paintChess(Chess chess) {
        char[][] charMap = chess.getCharMap();
        for (int i = 0; i < mapConfig.size; ++i) {
            for (int j = 0; j < mapConfig.size; ++j) {
                chessArray[i][j].setBackground(new Color(235, 235, 244));
            }
        }
        //TODO: use image to replace
        for (Point p : chess.getWalls()) {
            chessArray[p.getX()][p.getY()].setBackground(new Color(102, 51, 0));
        }
        for (Point p : chess.getHoles()) {
//            map[p.getX()][p.getY()]='O';
            chessArray[p.getX()][p.getY()].setBackground(Color.BLACK);
        }
        for (Point p : chess.getEggs()) {
//            map[p.getX()][p.getY()]='*';
            chessArray[p.getX()][p.getY()].setBackground(Color.YELLOW);
        }
        for (Snake snake : chess.getSnakes()) {
            for (Point p : snake.getAllPoint())
                if (p.getX() < mapConfig.size)
                    chessArray[p.getX()][p.getY()].setBackground(Color.GREEN);
        }
    }

    private JTable createUserTable(String[] usernames) {
        Object[] titles = new Object[]{
                "Number",
                "Name",
                "Lives",
                "Points"
        };
        Object[][] vals = new Object[5][4];
        for (int i = 0; i < mapConfig.nPlayer; i++) {
            vals[i][0] = i + 1;
            vals[i][1] = usernames[i];
            vals[i][2] = mapConfig.getnLives();
            vals[i][3] = 0;
        }
        return new JTable(vals, titles);
    }

    private void paintBlock(JPanel jPanel, Color color) {
        jPanel.setBackground(color);
        jPanel.repaint();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void setStatusText(String statusText) {
        statusJLabel.setText(statusText);
        if (statusText == "Pause")
            pauseButton.setText("Continue\n(Cancel my pause)");
        else
            pauseButton.setText("Pause");
    }

    public void addChatMessage(String username, String msg) {
        chatListPane.append(username + ": " + msg + "\n");
    }


    //------------------------
    // Following is play music
    //------------------------

    private Clip clip = null;
    private String filepath = null;
    private FileDialog fileDialogOpen = null;

    /**
     * @throws NullPointerException 没有选择文件
     */
    public void setFile() throws NullPointerException {
        fileDialogOpen = new FileDialog(outerFrame, "Open", FileDialog.LOAD);
        fileDialogOpen.setFile("*.wav");
        fileDialogOpen.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("wav");
            }
        });
        fileDialogOpen.setVisible(true);
        filepath = Paths.get(fileDialogOpen.getDirectory(), fileDialogOpen.getFile()).toString();
        System.out.print(filepath);
    }

    /**
     * 循环播放音乐，在播放前，必须先setFile
     *
     * @return
     */
    public boolean play(URL url) {
        try {
            // Open an audio input stream.
//            URL url = Paths.get("/home/eric/Downloads/autumn.wav").toUri().toURL();
//            URL url = Paths.get("/home/eric/Downloads/bee.wav").toUri().toURL();
//            URL url = Paths.get("/home/eric/Downloads/Airport-lounge-music-for-airports.zip").toUri().toURL();

//            URL url = Paths.get(filepath).toUri().toURL();
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.setLoopPoints(0, -1);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
//            System.out.print(clip.getFrameLength());
            return true;
        } catch (UnsupportedAudioFileException e) {
            JOptionPane.showMessageDialog(outerFrame, "Only wav audio file is supported",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(outerFrame, "Cannot open file",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopMusic() {
        clip.stop();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        cp = new JPanel();
        cp.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        cp.setFocusable(true);
        chessPanel = new JPanel();
        chessPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        chessPanel.setFocusable(false);
        cp.add(chessPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setFocusable(false);
        cp.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(0, 0), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        cp.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        chatListPane = new JTextArea();
        chatListPane.setFocusable(false);
        scrollPane1.setViewportView(chatListPane);
        sendMessageJTextField = new JTextField();
        sendMessageJTextField.setRequestFocusEnabled(true);
        sendMessageJTextField.setText("");
        panel2.add(sendMessageJTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        tabbedPane1.setFocusable(false);
        cp.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Game", panel3);
        pauseButton = new JButton();
        pauseButton.setFocusable(false);
        pauseButton.setRequestFocusEnabled(false);
        pauseButton.setText("Pause");
        panel3.add(pauseButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setEnabled(false);
        panel3.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        userTable.setEnabled(false);
        userTable.setFocusable(false);
        scrollPane2.setViewportView(userTable);
        final JLabel label1 = new JLabel();
        label1.setFocusable(false);
        label1.setText("Game Status");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusJLabel = new JLabel();
        statusJLabel.setFocusable(false);
        statusJLabel.setText("Running");
        panel3.add(statusJLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Music", panel4);
        playDefaultMusicButton = new JButton();
        playDefaultMusicButton.setFocusable(false);
        playDefaultMusicButton.setText("Play default music");
        panel4.add(playDefaultMusicButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setEnabled(false);
        stopButton.setFocusable(false);
        stopButton.setText("Stop");
        panel4.add(stopButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        localMusicButton = new JButton();
        localMusicButton.setFocusPainted(true);
        localMusicButton.setFocusable(false);
        localMusicButton.setText("Play local music");
        panel4.add(localMusicButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Music name");
        panel4.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        musicNameLabel = new JLabel();
        musicNameLabel.setText("Autumn (Default)");
        panel4.add(musicNameLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cp;
    }
}
