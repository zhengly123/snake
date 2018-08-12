package Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RankingWindow extends JFrame {
    public RankingWindow(ArrayList<String >names,ArrayList<Integer> points) throws HeadlessException {

        Object[] titles = new Object[]{
                "Ranking",
                "Name",
                "Points"
        };
        Object[][] vals = new Object[10][3];

        while (names.size() > 10) {
            names.remove(names.size() - 1);
        }

        for (int i = 0; i < names.size(); i++) {
            vals[i][0] = i+1;
            vals[i][1] = names.get(i);
            vals[i][2] = points.get(i);
        }
        for (int i = names.size(); i < 10; ++i) {
            vals[i][0] = i + 1;
            vals[i][1] = "Nobody";
            vals[i][2] = 0;
        }
        JTable jTable= new JTable(vals, titles);
        jTable.setEnabled(false);
//        JScrollPane jScrollPane=new JScrollPane();
//        jScrollPane.add(jTable);
        getContentPane().setLayout(new FlowLayout());
//        getContentPane().add(jScrollPane);
        this.setLayout(new BorderLayout());
        this.add("North",new JLabel("Ranking List"));
        this.add(jTable);
//        this.add(jScrollPane);
        setResizable(false);

        setSize(300,300);
        setVisible(true);
    }

    public static void main(String[] args) {
        ArrayList<String> names=new ArrayList<String>();
        ArrayList<Integer> points=new ArrayList<>();
        names.add("a");
        points.add(1);
        new RankingWindow(names,points);
    }
}
