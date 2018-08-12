package Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BlockPanel extends JPanel {

    Image image=null;
//    static {
//        try {
//            image= ImageIO.read(new File("/home/eric/IdeaProjects/snake/src/head12.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void setImage(Image image) {
        this.image = image;
    }

//    public void paint(Graphics g){
    public void paintComponent(Graphics g){
//        super.paint(g);
        super.paintComponent(g);
        try {
            g.drawImage(image, 0, 0,getSize().width, getSize().height, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
