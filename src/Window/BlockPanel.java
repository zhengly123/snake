package Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BlockPanel extends JPanel {

//    Image image = null;
    ArrayList<Image> imageList=new ArrayList<>();
    boolean refresh=false;
//    static {
//        try {
//            image= ImageIO.read(new File("/home/eric/IdeaProjects/snake/src/head12.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public BlockPanel() {
    }

    public BlockPanel(boolean refresh) {
        this.refresh = refresh;
    }

    public void setImage(Image image) {
//        this.image = image;
        imageList.add(image);
    }

    //    public void paint(Graphics g){
    public void paintComponent(Graphics g) {
//        super.paint(g);
        super.paintComponent(g);
        try {
            if (imageList.size() == 0) {
//                g.drawImage(null, 0, 0, getSize().width, getSize().height, null);
            } else {
                for (Image image:imageList)
                    g.drawImage(image, 0, 0, getSize().width, getSize().height, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (refresh)
            imageList.clear();
    }

}
