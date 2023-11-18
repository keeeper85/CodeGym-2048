package com.codegym.task.task35.task3513;

import java.awt.*;
import java.util.TreeMap;

public class Tile {

    int value;

    public Tile(int value) {
        this.value = value;
    }

    public Tile() {
        this.value = 0;
    }

    public boolean isEmpty(){
        if (value == 0) return true;
        return false;
    }

    public Color getFontColor(){
        if (value < 16) return new Color(0x776e65);
        return new Color(0xf9f6f2);
    }

    public Color getTileColor(){
        TreeMap<Integer, Color> colorMap = new TreeMap<>();

        colorMap.put(0, new Color(0xcdc1b4));
        colorMap.put(2, new Color(0xeee4da));
        colorMap.put(4, new Color(0xede0c8));
        colorMap.put(8, new Color(0xf2b179));
        colorMap.put(16, new Color(0xf59563));
        colorMap.put(32, new Color(0xf67c5f));
        colorMap.put(64, new Color(0xf65e3b));
        colorMap.put(128, new Color(0xedcf72));
        colorMap.put(256, new Color(0xedcc61));
        colorMap.put(512, new Color(0xedc850));
        colorMap.put(1024, new Color(0xedc53f));
        colorMap.put(2048, new Color(0xedc22e));

        if (colorMap.containsKey(value)) return colorMap.get(value);

        return new Color(0xff0000);

        }

}
