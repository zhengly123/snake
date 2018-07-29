package entity;

import config.MapConfig;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

enum Status {EMPTY,EGG,HOLE,SNAKE,WALL,INVALID,SNAKETAIL,INHOLE};
public class Chess {
    static final int maxTryTimeInGen=1000;

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    private MapConfig mapConfig;
    private ArrayList<Point> walls;
    private ArrayList<Snake> snakes;
    private ArrayList<Point> holes;
    private ArrayList<Point> eggs;
//    boolean[] holeStatus;
    //TODO:add loser log

    public Chess(MapConfig mapConfig) {
        this.mapConfig = mapConfig;
        walls=new ArrayList<>();
        snakes=new ArrayList<>();
        holes=new ArrayList<>();
        eggs=new ArrayList<>();
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
        for (int i=0;i<mapConfig.getnSnakes();++i)
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
     * @deprecated
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
        for (char[] column : map) {
            for (char c:column)
                c=' ';
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


    public void printMap() {
        char[][] chess= getCharMap();
        for (char[] row:chess)
        {
            for (char c:row)
                System.out.print(c);
            System.out.println();
        }
        for (int i=0;i<getMapConfig().nPlayer;++i)
            snakes.get(i).print(i);
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

    private boolean[] checkLose() {
        boolean[] lose=new boolean[getMapConfig().nPlayer];
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            lose[i]=checkHeadConfilct(i);
        }
        return lose;
    }

    public boolean[] move(int[] direction) {
        //move all snake but not modify chess
        Set<Integer> eatenEggs=new TreeSet<>();
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            eatenEggs.addAll(snakeMove(i, direction[i]));
        }

        //check lose
        boolean[] lose;
        int nLose=0;
        lose=checkLose();
        for (int i = 0; i < getMapConfig().nPlayer; ++i) {
            if (lose[i]) ++nLose;
        }
        if (nLose>0)
            return lose;
        //TODO:Add multiplayer support

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
