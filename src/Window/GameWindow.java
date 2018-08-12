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
    private JPanel inHolePanel;
    //    private JScrollPane userList;
    //    private JTextArea sendMessageTextArea;
    MapConfig mapConfig;
    ClientSocket clientSocket;
    BlockPanel[][] chessArray;
    private JFrame outerFrame;
    private Image headImage[][], bodyImage[], tailImage[][], eggImage, holeImage;
    private Image wallImage, grassImage, collisionsImage[];

    void readImage() {
        bodyImage = new Image[4];
        headImage = new Image[4][];
        collisionsImage = new Image[3];
        Image image = null;
        //TODO: produce other 2 image
        grassImage = new ImageIcon(this.getClass().getResource("/res/grass.jpg")).getImage();
        eggImage = new ImageIcon(this.getClass().getResource("/res/egg.png")).getImage();
        holeImage = new ImageIcon(this.getClass().getResource("/res/hole.png")).getImage();
        wallImage = new ImageIcon(this.getClass().getResource("/res/wall.png")).getImage();
        for (int i = 0; i < 3; ++i) {
            collisionsImage[i] = new ImageIcon(this.getClass().getResource("/res/exp" + i + ".png")).getImage();
        }

        for (int i = 0; i < 2; ++i) {
            bodyImage[i] = new ImageIcon(this.getClass().getResource("/res/body" + i + ".jpg")).getImage();
            headImage[i] = new Image[4];
            for (int j = 0; j < 4; ++j) {
                System.out.println("/res/head" + i + j + ".png");
                headImage[i][j] = new ImageIcon(this.getClass().getResource("/res/head" + i + j + ".png")).getImage();
            }
        }
    }

    public GameWindow(MapConfig mapConfig, ClientSocket clientSocket, JFrame outerFrame, String[] usernames) {
        this.mapConfig = mapConfig;
        this.clientSocket = clientSocket;
        this.outerFrame = outerFrame;
        readImage();

        GridLayout gridLayout = new GridLayout(mapConfig.size, mapConfig.size);
        //create UserList
        userTable = createUserTable(usernames);
        chessPanel = new BlockPanel();
        ((BlockPanel) chessPanel).setImage(grassImage);
        inHolePanel = new BlockPanel();
        ((BlockPanel) inHolePanel).setImage(holeImage);
        inHolePanel.setVisible(false);
        $$$setupUI$$$();

        chessPanel.setLayout(gridLayout);
        chessArray = new BlockPanel[mapConfig.size][];
        for (int i = 0; i < mapConfig.size; ++i) {
            chessArray[i] = new BlockPanel[mapConfig.size];
            for (int j = 0; j < mapConfig.size; ++j) {
                chessArray[i][j] = new BlockPanel(true);
                chessArray[i][j].setBackground(null);
                chessArray[i][j].setOpaque(false);
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

    public void setItemEnabled(boolean bool) {
        pauseButton.setEnabled(bool);
        statusJLabel.setText("Ended");
    }

    public void updateUserList(Chess chess, boolean[] playerOnline) {
        for (int i = 0; i < mapConfig.nPlayer; ++i) {
            userTable.setValueAt(chess.getLives()[i], i, 2);
            userTable.setValueAt(chess.getPoints()[i], i, 3);
            userTable.setValueAt(playerOnline[i] ? "Online" : "Offline", i, 4);
        }
    }

    public void paintChess(Chess chess) {
//        g.drawImage(image, 0, 0, 550, 400, null);char[][] charMap = chess.getCharMap();
        boolean inHole = false;
        for (int i = 0; i < mapConfig.nPlayer; ++i) {
            if (chess.getSnakes(i).getHeadInHole() >= 0) {
                inHole = true;
            }
        }
        inHolePanel.setVisible(inHole);

        for (int i = 0; i < mapConfig.size; ++i) {
            for (int j = 0; j < mapConfig.size; ++j) {
//                chessArray[i][j].setBackground(new Color(235, 235, 244));
//                chessArray[i][j].setImage(null);
                chessArray[i][j].setOpaque(false);
            }
        }
        //TODO: use image to replace
        for (Point p : chess.getWalls()) {
//            chessArray[p.getX()][p.getY()].setBackground(new Color(102, 51, 0));
            chessArray[p.getX()][p.getY()].setImage(wallImage);
        }
        for (Point p : chess.getHoles()) {
//            map[p.getX()][p.getY()]='O';
//            chessArray[p.getX()][p.getY()].setBackground(Color.BLACK);
            chessArray[p.getX()][p.getY()].setImage(holeImage);
        }
        for (Point p : chess.getEggs()) {
//            map[p.getX()][p.getY()]='*';
//            chessArray[p.getX()][p.getY()].setBackground(Color.YELLOW);
            chessArray[p.getX()][p.getY()].setImage(eggImage);
        }
//        for (Snake snake : chess.getSnakes()) {
        Snake snake;
        for (int i = 0; i < mapConfig.nPlayer; ++i) {
            snake = chess.getSnakes(i);
            for (Point p : snake.getAllPoint())
                if (p.getX() < mapConfig.size && p != snake.getHead()) {
                    chessArray[p.getX()][p.getY()].setImage(bodyImage[i]);
//                    chessArray[p.getX()][p.getY()].setBackground(Color.GREEN);
                }
            if (snake.getHead().getX() < mapConfig.size)
                chessArray[snake.getHead().getX()][snake.getHead().getY()].setImage(
                        headImage[i][snake.headDirection]);
        }

        if (chess.getCollisionsPoint() != null) {
            for (int i = 0; i < chess.getCollisionsPoint().size(); ++i) {
                chessArray[chess.getCollisionsPoint().get(i).getX()]
                        [chess.getCollisionsPoint().get(i).getY()].setImage(
                        collisionsImage[chess.getCollisionFrames().get(i)]);
            }
        }

        for (int i = 0; i < mapConfig.size; ++i) {
            for (int j = 0; j < mapConfig.size; ++j) {
                chessArray[i][j].setBackground(new Color(0, 0, 0, 0));
                chessArray[i][j].repaint();
            }
        }
    }

    private JTable createUserTable(String[] usernames) {
        Object[] titles = new Object[]{
                "Number",
                "Name",
                "Lives",
                "Points",
                "Status"
        };
        Object[][] vals = new Object[mapConfig.nPlayer][5];
        for (int i = 0; i < mapConfig.nPlayer; i++) {
            vals[i][0] = i + 1;
            vals[i][1] = usernames[i];
            vals[i][2] = mapConfig.getnLives();
            vals[i][3] = 0;
            vals[i][4] = "Online";
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
        cp.setLayout(new GridBagLayout());
        cp.setFocusable(true);
        chessPanel.setBackground(new Color(-1381388));
        chessPanel.setFocusable(false);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        cp.add(chessPanel, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setPreferredSize(new Dimension(300, 59));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        cp.add(panel1, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        chatListPane = new JTextArea();
        chatListPane.setFocusable(false);
        scrollPane1.setViewportView(chatListPane);
        sendMessageJTextField = new JTextField();
        sendMessageJTextField.setRequestFocusEnabled(true);
        sendMessageJTextField.setText("");
        panel1.add(sendMessageJTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        tabbedPane1.setFocusable(false);
        tabbedPane1.setPreferredSize(new Dimension(300, 517));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        cp.add(tabbedPane1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Game", panel2);
        pauseButton = new JButton();
        pauseButton.setFocusable(false);
        pauseButton.setRequestFocusEnabled(false);
        pauseButton.setText("Pause");
        panel2.add(pauseButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setEnabled(false);
        panel2.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        userTable.setEnabled(false);
        userTable.setFocusable(false);
        scrollPane2.setViewportView(userTable);
        final JLabel label1 = new JLabel();
        label1.setFocusable(false);
        label1.setText("Game Status");
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusJLabel = new JLabel();
        statusJLabel.setFocusable(false);
        statusJLabel.setText("Running");
        panel2.add(statusJLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(inHolePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(70, 70), null, new Dimension(70, 70), 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Music", panel3);
        playDefaultMusicButton = new JButton();
        playDefaultMusicButton.setFocusable(false);
        playDefaultMusicButton.setText("Play default music");
        panel3.add(playDefaultMusicButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setEnabled(false);
        stopButton.setFocusable(false);
        stopButton.setText("Stop");
        panel3.add(stopButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        localMusicButton = new JButton();
        localMusicButton.setFocusPainted(true);
        localMusicButton.setFocusable(false);
        localMusicButton.setText("Play local music");
        panel3.add(localMusicButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Music name");
        panel3.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        musicNameLabel = new JLabel();
        musicNameLabel.setText("Autumn (Default)");
        panel3.add(musicNameLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cp;
    }
}
