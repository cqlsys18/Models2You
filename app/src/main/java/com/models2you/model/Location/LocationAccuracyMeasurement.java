package com.models2you.model.Location;

import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;

import com.models2you.model.util.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by amitsingh on 1/20/2017.
 * Location Accuracy measurement
 */

public class LocationAccuracyMeasurement {
    private static LogFactory.Log log = LogFactory.getLog(LocationAccuracyMeasurement.class);

    // Constants
    private static final float REQUIRED_GPS_ACCURACY = 20;
    private static final float REQUIRED_NETWORK_ACCURACY = 50;
    private static final float FALLBACK_ACCURACY = 100;
    private static final int MAX_QUEUE_COUNT = 5;

    // location queue list - for fallback
    private static List<Location> locationQueueList;

    private static LocationAccuracyMeasurement locationAccuracy;

    private LocationAccuracyMeasurement() {}

    public static LocationAccuracyMeasurement get() {
        log.verbose("LocationAccuracyMeasurement ");
        if (locationAccuracy == null) {
            locationAccuracy = new LocationAccuracyMeasurement();
            locationQueueList = new CopyOnWriteArrayList<>();
        }
        return locationAccuracy;
    }

    /**
     * method to feed location into queue
     * @param location : location
     * @return location instance after saving to queue
     */
    public synchronized Location feedLocation(Location location) {
        if (location != null) {
            float minAccForProvider = TextUtils.equals(location.getProvider(), LocationManager.GPS_PROVIDER) ? REQUIRED_GPS_ACCURACY : REQUIRED_NETWORK_ACCURACY;
            if (location.getAccuracy() <= minAccForProvider) { //Good enough
                locationQueueList.clear();
                return location;
            } else { //Queue the location
                locationQueueList.add(location);
                if (locationQueueList.size() >= MAX_QUEUE_COUNT) { //Queue size reached limit
                    return iterateLocationFromQueue();
                }
            }
        }
        return location;
    }

    /**
     *  iterateLocationFromQueue : method to iterate location from queue
     */
    private synchronized Location iterateLocationFromQueue() {
        log.verbose("iterateLocationFromQueue");
        Location locationCandidate = locationQueueList.get(0);
        log.verbose("oldLocation Accuracy: " + locationCandidate.getAccuracy());
        for (int index = 1; index < locationQueueList.size(); index++) {
            Location nextLocation = locationQueueList.get(index);
            log.verbose("newLocation Accuracy: " + nextLocation.getAccuracy());
            if (nextLocation.getAccuracy() < locationCandidate.getAccuracy()) {
                locationCandidate = nextLocation;
            }
        }
        if (locationCandidate.getAccuracy() <= FALLBACK_ACCURACY) {
            log.verbose("fallbackLocationFromQueue Accuracy: " + locationCandidate.getAccuracy());
            return locationCandidate;
        }
        log.verbose("fallbackLocationFromQueue null");
        return null;
    }
}
