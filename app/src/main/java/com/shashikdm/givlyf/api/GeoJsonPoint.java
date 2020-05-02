package com.shashikdm.givlyf.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonPoint implements Serializable {
    private List<Double> coordinates;
    private String type;
    private Double x;
    private Double y;

    public GeoJsonPoint(Double y, Double x) {
        coordinates = new ArrayList<>();
        coordinates.add(x);
        coordinates.add(y);
        type = "point";
        this.x = x;
        this.y = y;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
