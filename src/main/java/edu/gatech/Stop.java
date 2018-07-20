package edu.gatech;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

public abstract class Stop {
    private Integer ID;
    private String stopName;
    private Double xCoord;
    private Double yCoord;
    private Integer transitStopId;
    private List<Rider> ridersAtStop;
    private HashMap<Integer, Integer> routesAtStop;


    public Stop() {
        this.ID = -1;
    }

    public Stop(int uniqueValue) {
        this.ID = uniqueValue;
        this.stopName = "";
        this.xCoord = 0.0;
        this.yCoord = 0.0;
        this.transitStopId=-1;
        this.routesAtStop =  new HashMap<Integer, Integer>();
        this.ridersAtStop = new ArrayList<>();
    }

    public Stop(int uniqueValue,String stopName) {
        this.ID = uniqueValue;
        this.stopName = stopName;
        this.xCoord = 0.0;
        this.yCoord = 0.0;
        this.transitStopId=-1;
        this.routesAtStop =  new HashMap<Integer, Integer>();
        this.ridersAtStop = new ArrayList<>();
    }

    public Stop(int uniqueValue, String inputName, double inputXCoord, double
            inputYCoord) {
        this.ID = uniqueValue;
        this.stopName = inputName;
        this.xCoord = inputXCoord;
        this.yCoord = inputYCoord;
        this.transitStopId = -1;
        this.routesAtStop = new HashMap<Integer, Integer>();
        this.ridersAtStop = new ArrayList<>();
   }

    public void setName(String inputName) { this.stopName = inputName; }

    public void setXCoord(double inputXCoord) { this.xCoord = inputXCoord; }

    public void setYCoord(double inputYCoord) { this.yCoord = inputYCoord; }

    public Integer getID() { return this.ID; }

    public String getName() { return this.stopName; }


    public Double getXCoord() { return this.xCoord; }

    public Double getYCoord() { return this.yCoord; }


    public Integer getTransitStopId() {
        return transitStopId;
    }

    public void setTransitStopId(Integer transitStopId) {
        this.transitStopId = transitStopId;
    }

    public void displayEvent() {
        System.out.println(" bus stop: " + Integer.toString(this.ID));
    }

    public void takeTurn() {
        System.out.println("get new people - exchange with bus when it passes by");
    }

    public Map<Integer,Integer> getRoutesAtStop() {
        return routesAtStop;
    }

    public void setRoutesAtStop(HashMap<Integer,Integer> routesAtStop) {
        this.routesAtStop = routesAtStop;
    }

    public List<Rider> getRidersAtStop() {
        return ridersAtStop;
    }

    public void setRidersAtStop(List<Rider> ridersAtStop) {
        this.ridersAtStop = ridersAtStop;
    }

    public void addNewRoute(int routeID) { this.routesAtStop.put(routesAtStop.size(), routeID); }

    public Double findDistance(Stop destination) {
        // coordinates are measure in abstract units and conversion factor translates to statute miles
        final double distanceConversion = 70.0;
        return distanceConversion * Math.sqrt(Math.pow((this.xCoord - destination.getXCoord()), 2) + Math.pow((this.yCoord - destination.getYCoord()), 2));
    }

    public List<Rider> getPassengersForRoute(int routeId) {
        List<Rider> returnList = new ArrayList<>();
        for (Rider riderObj:ridersAtStop){
            if (riderObj.getRoutes().contains(routeId))
                returnList.add(riderObj);
        }
        return returnList;
    }
    public List<Rider> exchangeRiders(int route,int rank, List<Rider> passengerForStop, int capacity) {


        int hourOfTheDay = (rank / 60) % 24;
        int ableToBoard;
        
        int tryingToBoard = getPassengersForRoute(route).size();
        int availableSeats = capacity-passengerForStop.size();

        List<Rider> newRiders=null;
        // update the number of passengers left waiting for the next bus
        if (tryingToBoard > availableSeats) {
            ableToBoard = availableSeats;
            //add passengers to bus
            //remove passengers for route,abletoBoard
            newRiders = reduceWaitingPassengers(route,ableToBoard);

        } else {
            ableToBoard = tryingToBoard;
            //add passengers to bus
            //remove passengers for Route at stop
            newRiders = reduceWaitingPassengers(route,ableToBoard);
        }

        // update the number of riders actually catching the bus and return the difference from the original riders
        return newRiders;
    }

    public List<Rider> reduceWaitingPassengers(int route,int num){
        List<Rider> passengersOnboarded = new ArrayList<Rider> ();
        List<Rider> passengersWaiting = new ArrayList<Rider> ();
        int i=0;
        for (Rider r: getPassengersForRoute(route)){
            if (i > num) break;
            passengersOnboarded.add(r);
            i++;
        }
        for (Rider r: ridersAtStop){
            if (!passengersOnboarded.contains(r))
                passengersWaiting.add(r);
        }
        this.ridersAtStop=passengersWaiting;

        return passengersOnboarded;
    }

    public void addNewRiders(Rider rider) { ridersAtStop.add(rider); }

    public void displayInternalStatus() {
        System.out.print("> stop - ID: " + Integer.toString(ID));
        for (int r:routesAtStop.values())
        System.out.print(" name: " + stopName + "route:  "+ r+ " waiting: " + ridersAtStop.size());
        System.out.println(" xCoord: " + Double.toString(xCoord) + " yCoord: " + Double.toString(yCoord));
    }



    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + stopName.hashCode();
        result = 31 * result + (xCoord != null ? xCoord.hashCode() : 0);
        result = 31 * result + (yCoord != null ? yCoord.hashCode() : 0);
        result = 31 * result + (ridersAtStop != null ? ridersAtStop.hashCode() : 0);
        result = 31 * result + (routesAtStop != null ? routesAtStop.hashCode() : 0);
        return result;
    }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Stop me = (Stop) object;
            if (this.ID == me.getID()) {
                result = true;
            }
        }
        return result;
    }

}
