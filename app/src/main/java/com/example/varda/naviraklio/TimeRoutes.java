package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Endelehion on 15/9/2017.
 */

public class TimeRoutes {
    LatLng origin, destination;
    double euclidDistance;
    double actualDistance;
    double euclidTravelTimeWithoutTraffic;
    double actualTravelTimeWithoutTraffic;
    double distanceDivergence,timeDivergence;

    public TimeRoutes(LatLng origin, LatLng destination, double euclidDistance, double actualDistance) {
        this.origin = origin;
        this.destination = destination;
        this.euclidDistance = euclidDistance;
        this.actualDistance = actualDistance;
        this.calcTravelTimes();
        this.distanceDivergence =actualDistance/euclidDistance;
        this.timeDivergence =getActualTravelTimeWithoutTraffic()/getEuclidTravelTimeWithoutTraffic();
    }

    public void calcTravelTimes() {
        actualTravelTimeWithoutTraffic = actualDistance * 0.0024;                      // 25km/h without traffic 1000m/2.4min = 1m/0.0024min
        euclidTravelTimeWithoutTraffic = euclidDistance * 0.0024;

    }

    public double getTimeDivergence() {
        return timeDivergence;
    }

    public void setTimeDivergence(double timeDivergence) {
        this.timeDivergence = timeDivergence;
    }

    public double getDistanceDivergence() {
        return distanceDivergence;
    }

    public void setDistanceDivergence(double distanceDivergence) {
        this.distanceDivergence = distanceDivergence;
    }

    public double getActualTravelTimeWithoutTraffic() {
        return actualTravelTimeWithoutTraffic;
    }

    public void setActualTravelTimeWithoutTraffic(double actualTravelTimeWithoutTraffic) {
        this.actualTravelTimeWithoutTraffic = actualTravelTimeWithoutTraffic;
    }

    public double getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(double actualDistance) {
        this.actualDistance = actualDistance;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public double getEuclidDistance() {
        return euclidDistance;
    }

    public void setEuclidDistance(double euclidDistance) {
        this.euclidDistance = euclidDistance;
    }

    public double getEuclidTravelTimeWithoutTraffic() {
        return euclidTravelTimeWithoutTraffic;
    }

    public void setEuclidTravelTimeWithoutTraffic(double euclidTravelTimeWithoutTraffic) {
        this.euclidTravelTimeWithoutTraffic = euclidTravelTimeWithoutTraffic;
    }
}
