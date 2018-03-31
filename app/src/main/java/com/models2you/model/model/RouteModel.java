package com.models2you.model.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amitsingh on 1/19/2017.
 * Route Information holder model
 */
public class RouteModel {
    private final int durationSeconds;
    private final double distanceMeter;
    private final List<LatLng> points;
    private LatLng northEastLatLng;
    private LatLng southWestLatLng;

    public RouteModel(int durationSeconds, double distanceMeter) {
        this.durationSeconds = durationSeconds;
        this.distanceMeter = distanceMeter;
        this.points = new ArrayList<>();
    }

    public void addPoints(List<LatLng> list) {
        points.addAll(list);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void addBoundArea(LatLng northEastLatLng, LatLng southWestLatLng) {
        this.northEastLatLng = northEastLatLng;
        this.southWestLatLng = southWestLatLng;
    }

    public LatLngBounds getBoundArea() {
        if (northEastLatLng != null && southWestLatLng != null) {
            return new LatLngBounds(southWestLatLng, northEastLatLng);
        }
        return null;
    }

    public LatLng getNorthEastLatLng() {
        return northEastLatLng;
    }

    public LatLng getSouthWestLatLng() {
        return southWestLatLng;
    }
}
