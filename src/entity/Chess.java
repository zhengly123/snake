package entity;

import config.MapConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

enum Status {EMPTY,EGG,HOLE,SNAKE,WALL,INVALID,SNAKETAIL,INHOLE};
public class Chess implements Serializable {
    static final int maxTryTimeInGen=1000;

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    private MapConfig mapConfig;
    private ArrayList<Point> walls;

    public ArrayList<Point> getWalls() {
        return walls;
    }

    public ArrayList<Snake> getSnakes() {
        return snakes;
    }

    public ArrayList<Point> getHoles() {
        return holes;
    }

    public ArrayList<Point> getEggs() {
        return eggs;
    }

    public Snake getSnakes(int i) {
        return snakes.get(i);
    }

    public ArrayList<Point> getCollisionsPoint() {
        return collisionsPoint;
    }

    public ArrayList<Integer> getCollisionFrames() {
        return collisionFrames;
    }

    public int[] getLives() {
        return lives;
    }

    public int[] getPoints() {
        return points;
    }

    private ArrayList<Snake> snakes;
    private ArrayList<Point> holes;
    private ArrayList<Point> eggs;
    private ArrayList<Point> collisionsPoint;
    private ArrayList<Integer> collisionFrames;
    private int[] lives,points;
//    boolean[] holeStatus;
    private static Logger logger=Logger.getLogger("Chess");
    //TODO:add loser log

    public Chess(MapConfig mapConfig) {
        this.mapConfig = mapConfig;
        walls=new ArrayList<>();
        snakes=new ArrayList<>();
        holes=new ArrayList<>();
        eggs=new ArrayList<>();
        lives=new int[mapConfig.nPlayer];
        points=new int[mapConfig.nPlayer];
        collisionFrames=new ArrayList<>();
        collisionsPoint=new ArrayList<>();
        for (int i = 0; i < mapConfig.nPlayer; ++i) {
            lives[i]=mapConfig.getnLives();
        }
        genWalls();
        genHoles();
        genEggs();
        initSnake();

//        holeStatus=new boolean[mapConfig.getnHoles()];
    }

    private void genWalls() {
        assert walls.size()==0;
        for (int i=0;i<mapConfig.getnWalls();++i)
        {
            Point p = genLegalPoint();
            walls.add(p);
        }
    }

    private void genHoles() {
        assert holes.size()==0;
        assert mapConfig.getnHoles()%2==0;
        for (int i=0;i<mapConfig.getnHoles();++i)
        {
            Point p = genLegalPoint();
            holes.add(p);
        }
    }


    private void genEggs() {
        assert eggs.size()==0;
        eggs.clear();
        for (int i=0;i<mapConfig.getnEggs();++i)
        {
            Point p = genLegalPoint();
            eggs.add(p);
        }
    }

    private Point genLegalPoint() {
        Point p;
        do {
            p = Point.random(mapConfig.size);
        } while (pointConflict(p) > 0);
        return p;
    }

    /**
     * 放置两条蛇
     */
    public void initSnake() {
        for (int i = 0; i<mapConfig.nPlayer;++i)
            snakes.add(genSnake(mapConfig.getSnakeInitLen()));
    }

    public void genSnake() {
        genSnake(mapConfig.getSnakeInitLen());
    }

    /**
     * 生成一条在地图中合法蛇
     * @param len
     * @return
     */
    public Snake genSnake(int len) {
        //TODO:判断蛇出生会不会就死掉
        Snake snake=null;
        int tryCnt;
        for (tryCnt=0;tryCnt<maxTryTimeInGen;++tryCnt)
        {
            snake=new Snake(len,mapConfig.size,genLegalPoint(),this);

            boolean legal=true;
            for (Point p : snake.getAllPoint()) {
                if (pointConflict(p)>0)
                {
                    legal=false;
                    break;
                }
            }
            if (legal)
                break;
        }
        assert tryCnt<maxTryTimeInGen;
        return snake;
    }

    /**
     * 判断该点在地图中的位置
     * @param point
     * @return 0表示空格
     * @Deprecated
     */
    public int pointConflict(Point point) {
        if (!point.inMap(mapConfig.size))
            return 5;
        for (Point x:walls)
            if (point.equals(x))
                return 4;
        for (Snake x:snakes)
            if (x.conflict(point))
                return 3;
        for (Point x:holes)
            if (point.equals(x))
                return 2;
        for (Point x:eggs)
            if (point.equals(x))
                return 1;
        return 0;
    }

    public Status pointConflictStatus(Point point) {
        Status ret=Status.EMPTY;
        if (!point.inMap(mapConfig.size))
            return Status.INVALID;
        for (Point x:walls)
            if (point.equals(x))
                return Status.WALL;
        for (Snake x:snakes)
            if (x.conflict(point)) {
                if (x.getTail().equals(point))
                    return Status.SNAKETAIL;
                else
                    return Status.SNAKE;
            }
        for (Point x:holes)
            if (point.equals(x))
                return Status.HOLE;
        for (int i=0;i<mapConfig.getnHoles();++i)
            if (point.getX()==-i&&point.getY()==-i)
                return Status.INHOLE;
        for (Point x:eggs)
            if (point.equals(x))
                return Status.EGG;
        return Status.EMPTY;
    }

    public Status[][] getStatusChess() {
        Status[][] chess = new Status[mapConfig.size][mapConfig.size];
        for (Status[] row : chess) {
            for (Status status:row)
                status=Status.EMPTY;
        }
        for (Point p:holes){
            chess[p.getX()][p.getY()]=Status.HOLE;
//            chess[p.getX()][p.getY()]=(chess[p.getX()][p.getY()]==Status.EMPTY)?Status.HOLE:Status.SNAKEONHOLE;
        }
        for (Point p:eggs){
            chess[p.getX()][p.getY()]= Status.EGG;
        }
        for (Snake snake : snakes) {
            for (Point p:snake.getAllPoint())
                chess[p.getX()][p.getY()]= Status.SNAKE;
        }
        for (Point p:walls){
            chess[p.getX()][p.getY()]= Status.WALL;
        }
        return chess;

    }

    public char[][] getCharMap() {
        char[][] map=new char[mapConfig.size][mapConfig.size];
//        for (int i=0;i<mapConfig.size;++i)
//            for (int i=0;i<mapConfig.size;++i)
        for (char[] row : map) {
            for (int i=0;i<getMapConfig().getSize();++i)
                row[i]=' ';
        }
        for (Point p:walls){
            map[p.getX()][p.getY()]='W';
        }
        for (Point p:holes){
            map[p.getX()][p.getY()]='O';
        }
        for (Point p:eggs){
            map[p.getX()][p.getY()]='*';
        }
        for (Snake snake : snakes) {
            for (Point p:snake.getAllPoint())
                if (p.getX()<mapConfig.size)
                    map[p.getX()][p.getY()]='a';
        }
        return map;
    }

    public void eatEgg(Point point) {
        int i;
        for (i = 0; i < eggs.size(); ++i) {
            if (point.equals(eggs.get(i)))
                break;
        }
        assert i<eggs.size();
        eggs.remove(i);
    }

    public void genSnakeFromHoles(int index) {
        Snake snake;
        boolean success=false;
        for (int i = 0; i < mapConfig.getnHoles(); ++i) {
            snake=new Snake(new Point(mapConfig.size+i/2,1),this,i);
            snakes.set(index, snake);
            if (!checkHeadConfilct(index)) {
                success=true;
//                s//TODO:蛇没有从洞里出来，到底是从哪里生成的？？
                break;
            }
        }
        if (!success) {
            snakes.set(index, genSnake(mapConfig.getSnakeInitLen()));
            logger.info("Fail to produce snake in holes");
        }
        else
            logger.info("Succeed to produce snake in holes");
    }

    public void printMap() {
        char[][] chess= getCharMap();
        for (char[] row:chess)
        {
            for (Character c:row)
                System.out.print(c);
            System.out.println();
        }
        for (int i=0;i<getMapConfig().nPlayer;++i)
            snakes.get(i).print(i);

        System.out.println("Lives array");
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            System.out.printf("%2d ",lives[i]);
        }
        System.out.println("");

        System.out.println("Points array");
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            System.out.printf("%2d ",points[i]);
        }
        System.out.println("");
    }

    ///-------------------------------------

    private Set<Integer> snakeMove(int c, int direction) {
        Snake cSnake=snakes.get(c);
        Point head=cSnake.getHead(),newHead=null;
        Set<Integer> eatenEggs=new TreeSet<>();

        //若方向是当前行进的反方向，则无效，延续原有方向
        if ((direction+2)%4==cSnake.headDirection)
            direction=(direction+2)%4;

        if (head.getX() >= getMapConfig().size) {//if head in hole
            if (head.getY() < getMapConfig().getHoleLen() - 1) {//do not reach the end of hole
                cSnake.addHead(cSnake.headDirection,new Point(head.getX(),head.getY()+1));
            }
            else {//appear on the corresponding hole
                cSnake.addHead(direction,holes.get(cSnake.getHeadInHole()^1));
            }
            cSnake.removeTail();
        } else if (holes.indexOf(head) < 0||cSnake.getHeadInHole()>=0) {//if head is on normal chess
            cSnake.setHeadInHole(-1);//snake leave hole completely
            newHead = head.move(direction, mapConfig.size);
            cSnake.addHead(direction, newHead);
            if (eggs.indexOf(newHead) < 0) {
                cSnake.removeTail();
            } else {
                eatenEggs.add(eggs.indexOf(newHead));
            }
        } else {//if head is on a hole
            int holeNumber = holes.indexOf(head);
            newHead = new Point(holeNumber / 2 + getMapConfig().size, 0);
            cSnake.setHeadInHole(holeNumber);
            cSnake.addHead(cSnake.headDirection, newHead);
            cSnake.removeTail();
        }
        snakes.get(c).setInHole();
        return eatenEggs;
    }

    private boolean checkHeadConfilct(int c) {
        Point head=snakes.get(c).getHead();
        if (walls.indexOf(head)>=0)
            return true;

        for (int j = 0; j < getMapConfig().nPlayer; ++j) {
            if (j != c) {
                if (snakes.get(j).conflict(head))
                    return true;
            }
        }

        return false;
    }

    private boolean checkSnakePointConfilct(int c) {
        for (Point head : snakes.get(c).getBodies()) {
//            Point head=snakes.get(c).getHead();

            if (walls.indexOf(head)>=0)
                return true;

            for (int j = 0; j < getMapConfig().nPlayer; ++j) {
                if (j != c) {
                    if (snakes.get(j).conflict(head))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean[] checkLose() {
        boolean[] lose=new boolean[getMapConfig().nPlayer];
        //输的玩家在死亡后下一个Round消失
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (lives[i] == 0) {
                snakes.set(i,Snake.getNullSnake());
            }
        }
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            lose[i]=checkHeadConfilct(i);
            if (lose[i]) {
                collisionFrames.add(2);
                collisionsPoint.add(snakes.get(i).getHead());
            }
        }

        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (!lose[i])
                continue;
            lives[i]--;
            if (lose[i] && lives[i] > 0) {
                genSnakeFromHoles(i);
//                snakes.set(i, genSnake(mapConfig.getSnakeInitLen()));
                lose[i] = false;
            }
        }
        return lose;
    }

    public boolean[] move(int[] direction) {
        //move all snake but not modify chess
        Set<Integer> eatenEggs=new TreeSet<>();
        int[] oldLen=new int[getMapConfig().nPlayer];

        for (int i = collisionsPoint.size() - 1; i >= 0; --i) {
            if (collisionFrames.get(i) == 0) {
                collisionFrames.remove(i);
                collisionsPoint.remove(i);
            }
            else
                collisionFrames.set(i,collisionFrames.get(i)-1);
        }

        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            oldLen[i]=snakes.get(i).getLen();
        }
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (lives[i]==0)
                continue;
            eatenEggs.addAll(snakeMove(i, direction[i]));
        }

        //check lose
        boolean[] lose;
        int nLose=0;
        lose=checkLose();

        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (snakes.get(i).getLen() > oldLen[i]) {
                points[i]++;
            }
        }

        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (lose[i]) ++nLose;
        }
        if (nLose>0)
            return lose;

        updateEgg(eatenEggs);
        return new boolean[getMapConfig().nPlayer];
    }

    private void updateEgg(Set<Integer> eatenEggs) {
        for (int i = eggs.size(); i>=0; --i) {
            if (eatenEggs.contains(i))
                eggs.remove(i);
        }
        if (eggs.size() == 0) {
            genEggs();
        }
    }
}
