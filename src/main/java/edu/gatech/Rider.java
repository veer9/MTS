package edu.gatech;

import java.util.ArrayList;
import java.util.List;

/**
 * Rider
 */
public class Rider {

    Integer riderId;
    List<Integer> routes;
    Integer startStopId;
    Integer destStopId;
    Integer travelMode;//0-Busonly,1-Trainonly,2-BusandTrain- Defaults to bus only
    List<Integer> transitStops;
    Integer transitCount;


    public Rider(Integer riderId, List<Integer> routes, Integer startStopId, Integer destStopId, Integer travelMode) {
        this.riderId = riderId;
        this.routes = routes;
        this.startStopId = startStopId;
        this.destStopId = destStopId;
        this.travelMode = travelMode;
        this.transitStops = new ArrayList<Integer>();
        this.transitCount=0;
    }

    public Rider(Integer riderId, List<Integer> routes, Integer startStopId, Integer destStopId) {
        this.riderId = riderId;
        this.routes = routes;
        this.startStopId = startStopId;
        this.destStopId = destStopId;
        travelMode=0;
        this.transitStops = new ArrayList<Integer>();
        this.transitCount=0;
    }

    public Rider(){}
    public Integer getRiderId() {
        return riderId;
    }

    public void setRiderId(Integer riderId) {
        this.riderId = riderId;
    }

    public List<Integer> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Integer> routes) {
        this.routes = routes;
    }

    public Integer getStartStopId() {
        return startStopId;
    }

    public void setStartStopId(Integer startStopId) {
        this.startStopId = startStopId;
    }

    public Integer getDestStopId() {
        return destStopId;
    }

    public void setDestStopId(Integer destStopId) {
        this.destStopId = destStopId;
    }

    public List<Integer> getTransitStops() {
        return transitStops;
    }

    public void setTransitStops(List<Integer> transitStops) {
        this.transitStops = transitStops;
    }

    public Integer getTransitCount() {
        return transitCount;
    }

    public void setTransitCount(Integer transitCount) {
        this.transitCount = transitCount;
    }


    public Integer getTravelMode() {
        return travelMode;
    }

    @Override
    public String toString() {
        return "Rider{" +
                "riderId=" + riderId +
                ", routes=" + routes +
                ", startStopId=" + startStopId +
                ", destStopId=" + destStopId +
                ", travelMode=" + travelMode +
                ", transitStops=" + transitStops +
                ", transitCount=" + transitCount +
                '}';
    }

    public void setTravelMode(Integer travelMode) {
        this.travelMode = travelMode;
    }

}
