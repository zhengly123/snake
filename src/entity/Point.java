package entity;

import java.util.Objects;
import java.util.Random;

public class Point {
    private int x,y;
    static Random randomInt = new Random(1015);
    static final int [][]move={{1,0},{0,1},{-1,0},{0,-1}};

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Boolean inMap(int size) {
        return x<0&&x==y||x>=0&&y>=0&&x<size&&y<size;
    }

    public static Point random(int size) {
        return new Point(randomInt.nextInt(size),randomInt.nextInt(size));
    }

    public Point move(int direction,int size)
    {
        return new Point((x+move[direction][0]+size)%size, (y+move[direction][1]+size)%size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return getX() == point.getX() &&
                getY() == point.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
