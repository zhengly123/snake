package main;

import config.MapConfig;
import entity.Chess;

public class DebugConsole {
    public static void main(String[] args) {
        MapConfig mapConfig=new MapConfig();
        Chess chess=new Chess(mapConfig);
//        System.out.print(chess.getCharMap().toString());
        chess.printMap();
    }
}
