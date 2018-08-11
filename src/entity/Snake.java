package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

enum HeadStatus{Normal,ReadyToInHole,InHole};

public class Snake implements Serializable {
    static final int [][]move={{1,0},{0,1},{-1,0},{0,-1}};
    static final Random randomInt = new Random(1015);

    private ArrayList<Point> bodies;
    private ArrayList<Point> joints;
    public int headDirection,tailDirection;
//    int len;
    HeadStatus headStatus;
    private boolean inHole;
    private int headInHole;//进洞编号
    private Chess chess;

    public static Snake getNullSnake() {
        return new Snake();
    }

    public Snake() {
        bodies=new ArrayList<>();
        joints=new ArrayList<>();
        chess=null;
    }

    public boolean isInHole() {
        return inHole;
    }

    /**
     * 检测snake是否在hole内
     */
    public void setInHole() {
        this.inHole =false;
        for (Point p : bodies) {
            if (p.getX() >= chess.getMapConfig().getSize()) {
                inHole = true;
                break;
            }
        }
    }

    public int getLen() {
        return bodies.size();
    }

    public Boolean conflict(Point point) {
        for (Point body:bodies)
            if (point.equals(body))
                return true;
        return false;
    }

    public Snake(int len, int mapSize, Point head, Chess chess) {
        int direction=randomInt.nextInt(4);
        bodies=new ArrayList<>();
        bodies.add(head);
        Point last=head;
        for (int j=0;j<len-1;++j)
        {
            last=new Point(last.getX()+move[direction][0],last.getY()+move[direction][1]);
            bodies.add(last);
        }

        headDirection=tailDirection=(direction+2)%4;
        joints=new ArrayList<>();
//        this.len=len;
        headStatus=HeadStatus.Normal;
        inHole =false;
        this.chess=chess;
    }

    public ArrayList<Point> getAllPoint() {
        return bodies;
    }

    public Point getHead() {
        return bodies.get(0);
    }

    public Point getTail() {
        return bodies.get(bodies.size()-1);
    }

//    /**
//     * 判断蛇头能否向一个方向移动
//     * @param direction
//     * @return 如果能，即空地或吃果子或空洞或尾巴，返回true。若不能，即死亡，返回false.
//     */
//    private boolean canMove(int direction) {
//        Point next;
//        //TODO:临时注释，应该有用的.应该移动到chess中实现
////        if (headStatus==HeadStatus.ReadyToInHole)
////            next=new Point(chess.getMapConfig().size+,-inHole);
////        else
////            next=getHead().move(direction,chess.getMapConfig().getSize());
////        Status status=chess.pointConflictStatus(next);
////        if (status==Status.EMPTY||status==Status.EGG||status==Status.HOLE||status==Status.SNAKETAIL||status==Status.INHOLE)
////            return true;
////        else
//            return false;
//    }

//    /**
//     * 按方向移动蛇。注意，需要事先保证可以移动。
//     * @param direction
//     */
//    public void move(int direction,Point newHead){
//        assert canMove(direction);
//        Point newHead=getHead().move(direction,chess.getMapConfig().getSize());
//        Status status=chess.pointConflictStatus(newHead);
//        bodies.add(0,newHead);
//        //TODO:canMove中没有考虑head在hole中的情况。下面的status要考虑舌头在Hole里面。Status要增加这一状态。
//        if (status == Status.EGG) {
//            chess.eatEgg(newHead);
//            return;
//        }
//        bodies.remove(bodies.size()-1);
//    }

    public void addHead(int direction, Point newHead) {
        bodies.add(0,newHead);
        headDirection=direction;
    }

    public void removeTail(){
        bodies.remove(bodies.size() - 1);
    }

    public void print(int id) {
        System.out.printf("Snake %d:",id);
        for (Point p : bodies) {
            System.out.print(p);
            System.out.print("<-");
        }
        System.out.println();
    }

    public int getHeadInHole() {
        return headInHole;
    }

    public void setHeadInHole(int headInHole) {
        this.headInHole = headInHole;
    }
}
