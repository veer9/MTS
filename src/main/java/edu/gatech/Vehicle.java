package edu.gatech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vehicle {
    private Integer ID;
    private Integer route;
    private Integer nextLocation;
    private Integer prevLocation;
    private List<Rider> passengers;
    private Integer capacity;
    private Integer speed; // given in statute miles per hour

    public Vehicle() {
        this.ID = -1;
    }

    public Vehicle(int uniqueValue) {
        this.ID = uniqueValue;
        this.route = -1;
        this.nextLocation = -1;
        this.prevLocation = -1;
        this.passengers = new ArrayList<>();
        this.capacity = -1;
        this.speed = -1;
    }

    public Vehicle(int uniqueValue, int inputRoute, int inputLocation, int inputCapacity, int
            inputSpeed) {
        this.ID = uniqueValue;
        this.route = inputRoute;
        this.nextLocation = inputLocation;
        this.prevLocation = inputLocation;
        this.passengers = new ArrayList<>();
        this.capacity = inputCapacity;
        this.speed = inputSpeed;
   }

    public void setRoute(int inputRoute) { this.route = inputRoute; }

    public void setLocation(int inputLocation) {
    	this.prevLocation = this.nextLocation;
    	this.nextLocation = inputLocation;
    }

    public void setPassengers(List<Rider> inputPassengers) { this.passengers = inputPassengers; }

    public void setCapacity(int inputCapacity) { this.capacity = inputCapacity; }

    public void setSpeed(int inputSpeed) { this.speed = inputSpeed; }

    public Integer getID() { return this.ID; }

    public Integer getRouteID() { return this.route; }

    public Integer getLocation() { return this.nextLocation; }

    public Integer getPastLocation() { return this.prevLocation; }

    public List<Rider> getPassengers() { return this.passengers; }

    public List<Rider> getPassengersForStop(int stopId) {
        List<Rider> returnList = new ArrayList<>();
        List<Rider> temp = this.passengers;
        for (Rider riderObj:temp){
            int count = riderObj.getTransitCount() ==null ? 0: riderObj.getTransitCount();
            if (riderObj.getTransitStops()!= null  && riderObj.getTransitStops().size() > 0) {
                if(riderObj.getTransitStops().get(count) != null)
                if (riderObj.getDestStopId() == stopId || riderObj.getTransitStops().get(count) ==
                        stopId) {
                    riderObj.setTransitCount(count++);
                    returnList.add(riderObj);
                }
            }
            else if (riderObj.getDestStopId() == stopId){
                returnList.add(riderObj);
            }
        }
        return returnList;
    }

    public void addPassengerstoVehicle(List<Rider> onBoard){
        List<Rider> riders = passengers;
        for (Rider r:onBoard){
            riders.add(r);
        }
        this.setPassengers(riders);
    }


    public void removeCurrentPassengers(List<Rider> currentPassengers){
        List<Rider> remainPassengers = new ArrayList<>();
        for(Rider r : passengers){
            if (!currentPassengers.contains(r)){
                remainPassengers.add(r);
            }
        }
        this.setPassengers(remainPassengers);
    }

    public Integer getCapacity() { return this.capacity; }

    public Integer getSpeed() { return this.speed; }

    public void displayEvent() {
        System.out.println(" bus: " + Integer.toString(this.ID));
    }

    public void displayInternalStatus() {
        System.out.print("> Vehicle - ID: " + Integer.toString(ID) + " route: " + Integer.toString(route));
        System.out.print(" location from: " + Integer.toString(prevLocation) + " to: " + Integer.toString(nextLocation));
        System.out.print(" passengers: " + Integer.toString(passengers.size()) + " capacity: " + Integer.toString(capacity));
        System.out.print(" passenger on board: " + Arrays.asList(passengers) );
        System.out.println(" speed: " + Integer.toString(speed));
    }

    public void takeTurn() {
        System.out.println("drop off passengers - pickup passengers to capacity - move to next stop");
    }

   // public void adjustPassengers(int differential) { passengers = passengers + differential; }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Vehicle me = (Vehicle) object;
            if (this.ID == me.getID()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (nextLocation != null ? nextLocation.hashCode() : 0);
        result = 31 * result + (prevLocation != null ? prevLocation.hashCode() : 0);
        result = 31 * result + (passengers != null ? passengers.hashCode() : 0);
        result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        return result;
    }
}
