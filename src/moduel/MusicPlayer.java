package moduel;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

class SoundClipTest extends JFrame {
    private Clip clip=null;
    private String filepath=null;
    private FileDialog fileDialogOpen=null;

    /**
     *
     * @throws NullPointerException 没有选择文件
     */
    public void setFile() throws NullPointerException{
        fileDialogOpen = new FileDialog(this, "Open", FileDialog.LOAD);
        fileDialogOpen.setFile("*.wav");
        fileDialogOpen.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("wav");
            }
        });
        fileDialogOpen.setVisible(true);
        filepath=Paths.get(fileDialogOpen.getDirectory(), fileDialogOpen.getFile()).toString();
        System.out.print(filepath);
    }

    /**
     * 循环播放音乐，在播放前，必须先setFile
     * @return
     */
    public boolean play(){
        try {
            // Open an audio input stream.
//            URL url = Paths.get("/home/eric/Downloads/autumn.wav").toUri().toURL();
//            URL url = Paths.get("/home/eric/Downloads/bee.wav").toUri().toURL();
//            URL url = Paths.get("/home/eric/Downloads/Airport-lounge-music-for-airports.zip").toUri().toURL();
            URL url = Paths.get(filepath).toUri().toURL();
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.setLoopPoints(0,-1);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
//            System.out.print(clip.getFrameLength());
            return true;
        } catch (UnsupportedAudioFileException e) {
            JOptionPane.showMessageDialog(this,"Only wav audio file is supported",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Cannot open file",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        clip.stop();
    }

//    public SoundClipTest() {
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JButton button=new JButton();
//        getContentPane().add(button);
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                clip.stop();
//            }
//        });
//
//        this.setSize(300, 200);
//        this.setVisible(true);
//
//        try {
//            // Open an audio input stream.
////            URL url = Paths.get("/home/eric/Downloads/autumn.wav").toUri().toURL();
////            URL url = Paths.get("/home/eric/Downloads/bee.wav").toUri().toURL();
//            URL url = Paths.get("/home/eric/Downloads/Airport-lounge-music-for-airports.zip").toUri().toURL();
//            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
//            // Get a sound clip resource.
//            clip = AudioSystem.getClip();
//            // Open audio clip and load samples from the audio input stream.
//            clip.open(audioIn);
//            clip.setLoopPoints(0,-1);
////            clip.start();
//            clip.loop(Clip.LOOP_CONTINUOUSLY);
//            System.out.print(clip.getFrameLength());
//        } catch (UnsupportedAudioFileException e) {
//            JOptionPane.showMessageDialog(this,"Only wav audio file is supported",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
//    }
//
    /**
     * Main for debug only
     * @param args
     */
    public static void main(String[] args) {
        SoundClipTest soundClipTest=new SoundClipTest();
        try {
            soundClipTest.setFile();
            soundClipTest.play();
        } catch (NullPointerException e) {
            //not select a file
//            e.printStackTrace();
        }
    }
}