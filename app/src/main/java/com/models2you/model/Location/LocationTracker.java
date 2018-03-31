package com.models2you.model.Location;

import android.location.Location;

import com.models2you.model.util.LogFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by amitsingh on 1/20/2017.
 * Basic Location Tracker Helper
 */
public class LocationTracker {
    private static final LogFactory.Log log = LogFactory.getLog(LocationTracker.class);
    private static LocationTracker instance = new LocationTracker();

    private List<LocationUpdateListener> listeners;

    public static LocationTracker getInstance() {
        return instance;
    }

    private LocationTracker() {
        listeners = new CopyOnWriteArrayList<>();
    }

    public synchronized void feedLocationUpdate(Location location) {
        if(location != null) {
            log.verbose("feedLocationUpdate called");
            if (listeners.size() > 0) {
                Iterator<LocationUpdateListener> listenerIterator = listeners.iterator();
                while (listenerIterator.hasNext()) {
                    try {
                        LocationUpdateListener listener = listenerIterator.next();
                        log.verbose("feedLocationUpdate on listener " + listener.getClass().getName());
                        listener.onLocationUpdate(location);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
    }

    public synchronized void registerListener(LocationUpdateListener listener) {
        log.verbose("registerListener " + listener);
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
        log.verbose("registerListener size: " + listeners.size());
    }

    public synchronized void unregisterListener(LocationUpdateListener listener) {
        log.verbose("unregisterListener " + listener);
        listeners.remove(listener);
    }

}
