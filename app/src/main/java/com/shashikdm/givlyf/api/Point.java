package com.shashikdm.givlyf.api;

import java.io.Serializable;

public class Point implements Serializable {
    private Double x;
    private Double y;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}
