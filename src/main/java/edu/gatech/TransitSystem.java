package edu.gatech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TransitSystem {
    private HashMap<Integer, Stop> stops;
    private HashMap<Integer, Route> routes;
    private HashMap<Integer, Set<TransitStop>>  railTransitStops;
    private HashMap<Integer, Set<TransitStop>>  busTransitStops;

    private int lastRiderId = -1;

    public TransitSystem() {
        stops = new HashMap<Integer, Stop>();
        routes = new HashMap<Integer, Route>();
        busTransitStops = new HashMap<Integer, Set<TransitStop>>();
        railTransitStops = new HashMap<Integer, Set<TransitStop>>();
    }

    public Stop getStop(int stopID) {
        if (stops.containsKey(stopID)) { return stops.get(stopID); }
        return null;
    }

    public Route getRoute(int routeID) {
        if (routes.containsKey(routeID)) { return routes.get(routeID); }
        return null;
    }

    public int getLastRiderId() {
        return lastRiderId;
    }

    /*
    public void setTransitStops(List<TransitStop> busTransitStops) {
        this.transitStops = busTransitStops;
    }
    */
    /*
    public void setRailTransitStops(HashMap<Integer, TransitStop> railTransitStops) {
        this.railTransitStops = railTransitStops;
    }
    */

    public void addTransitStops(TransitStop ts){


        Set temp;
        temp =busTransitStops.get(ts.getBusRoute());
        if (temp == null )
           temp = new HashSet();
        temp.add(ts);
        busTransitStops.put(ts.getBusRoute(),temp);
        Set tempRail ;
        tempRail = railTransitStops.get(ts.getRailRoute());
        if (tempRail == null )
            tempRail = new HashSet();
        tempRail.add(ts);
        railTransitStops.put(ts.getRailRoute(),tempRail);

        if (this.getStop(ts.getBusStop()) != null) this.getStop(ts.getBusStop()).setTransitStopId(ts.getRailStop());
        if (this.getStop(ts.getRailStop()) != null) this.getStop(ts.getRailStop()).setTransitStopId(ts.getBusStop());

    }

    /*
    public void addBusTransitStops(TransitStop ts){
        busTransitStops.put(ts.getBusRoute(),ts);
    }
    */
    public void makeTransitInfo(Integer railRoute, Integer railStop, Integer busRoute, Integer busStop){
        TransitStop tf = new TransitStop( railRoute,  railStop,  busRoute,  busStop);
        addTransitStops(tf);
       // addBusTransitStops(tf);
    }

    public int makeRider(int uniqueID, int start, int stop, String
            routes, int travelMode) {
        List<Integer> list = Stream.of(routes.split("-"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return makeRider( uniqueID,  start,  stop,
                list,  travelMode);
    }

    public int makeRider(int uniqueID, int start, int stop, List<Integer>
            routes, int travelMode) {
        lastRiderId = Math.max(uniqueID, lastRiderId + 1);

        if (stops.get(start)!= null) {
            Rider rider = new Rider(uniqueID, routes, start, stop,travelMode);
            if (routes.size() > 1 ) {
                List<Integer> ts = findTransitStopDetails(routes,travelMode);
                rider.setTransitStops(ts);

            }
            stops.get(start).addNewRiders(rider);
            return uniqueID;

        }
        return 0;
    }


    public List<Integer> findTransitStopDetails(List<Integer> routesList,  int travelMode){

        List<Integer> transitStopsList = new ArrayList<Integer>();

        int j = 0;
        while (j< routesList.size()-1) {

            Integer r1 = routesList.get(j);
            Integer r2 = routesList.get(j+1);
            Integer stopId=null;
            if (travelMode == 2) {
                Set<TransitStop> ts ;
                if(busTransitStops.get(r1) != null)
                    ts =  busTransitStops.get(r1);
                else
                    ts = railTransitStops.get(r1);

                for(TransitStop tStop : ts){
                    if (tStop.getBusRoute() == r2) {
                        stopId = tStop.getBusStop();
                        transitStopsList.add(tStop.getRailStop());
                        break;
                    }
                    else if (tStop.getRailRoute() == r2) {
                        stopId = tStop.getRailStop();
                        transitStopsList.add(tStop.getBusStop());
                        break;
                    }
                }

                transitStopsList.add(stopId);
            }
            //find the common stops between two routes
                else {
                    Route rt1 = getRoutes().get(r1);
                    Route rt2 = getRoutes().get(r2);
                    if (rt1!=null && rt2 != null) {
                        List<Integer> intersect = new ArrayList<Integer>(rt1.getAllStopsForRoute().values());
                        boolean intersection = intersect.retainAll(new ArrayList<Integer>(rt2
                                .getAllStopsForRoute().values()));
                        //pick the first intersection stop
                        transitStopsList.add(intersect.get(0));
                    }
                }
            j++;
        }
        return transitStopsList;
    }

    public void appendStopToRoute(int routeID, int nextStopID) { routes.get(routeID).addNewStop(nextStopID); }
    public void appendRouteToStop(int routeID, int nextStopID) { stops.get(nextStopID).addNewRoute(routeID); }

    public HashMap<Integer, Stop> getStops() { return stops; }

    public HashMap<Integer, Route> getRoutes() { return routes; }

    public List<TransitStop> getAllTransitStopsInSystem(){
        /*
        List<TransitStop> list= new ArrayList<TransitStop>();
        for (TransitStop ts:this.transitStops.values() )
            list.add(ts);
        return list;*/

        return (List)busTransitStops.values();
    }

  /*  public TransitStop getTransitStopForRoute(int routeId){
        return this.transitStops.get(routeId);

    }*/

    public void addTransitPassengersToStop(List<Rider> riders,int stopId,TransitSystem system){
        for (Rider rider: riders){
            if (rider.getTransitStops().size() > 0 && rider.getDestStopId() != stopId){
                //check if the next transit stop is on the routes at this stop
                //if not move this rider to trainStop
                if(rider.getTransitStops()!= null && rider.getTransitStops().size() > 1) {
                    int nextStop = rider.getTransitStops().get(rider.getTransitCount()+1);
                    System.out.println("Transit count " + rider.getTransitCount()+ " Next stop "+nextStop + " Stop ID " + stopId);
                    if (nextStop == getStop(stopId).getTransitStopId()) {
                        system.getStop(nextStop).addNewRiders(rider);

                    } else {
                        this.getStop(stopId).addNewRiders(rider);
                    }
                }
                else{
                    this.getStop(stopId).addNewRiders(rider);
                }
            }
        }
    }
    
    public abstract void displayModel() ;
}
