package edu.gatech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class SimQueue {
  private static PriorityQueue<SimEvent> eventQueue;
  private Comparator<SimEvent> simComparator;
  final static Integer passengerFrequency = 3;

  private int lastRank = 0;

  public SimQueue() {
    simComparator = new SimEventComparator();
    eventQueue = new PriorityQueue<SimEvent>(100, simComparator);
  }

  public interface QueueListener {
    void currentRank(int rank);
    void reportVehicleMoved(Vehicle vehicle);
    void printInformation(String string);
  }

  class DefaultQueueListener implements QueueListener {
    @Override
    public void reportVehicleMoved(Vehicle vehicle) { }

    @Override
    public void currentRank(int rank) { }

    @Override
    public void printInformation(String string) {
      System.out.println(string);
    }
  }

  public int getQueueSize() {
    return eventQueue.size();
  }


  public List<SimEvent> getListOfActiveEvents(){
    //new class of event type- event id
    List<SimEvent> eventList = new ArrayList<SimEvent>();
    Iterator itr = eventQueue.iterator();
    while (itr.hasNext()){
      eventList.add((SimEvent)itr.next());
    }
    return eventList;
  }

  public void addEvent(int timeRank, String eventType, int eventId){
    SimEvent se = new SimEvent(timeRank,eventType,eventId);
    eventQueue.add(se);
  }

  public void removeEvent(int timeRank, String eventType, int eventId){
    SimEvent se = new SimEvent(timeRank,eventType,eventId);
    eventQueue.remove(se);

  }

  public void triggerNextEvent(BusSystem busModel, RailSystem railModel, int hourOfDay) {
    triggerNextEvent(busModel, railModel, hourOfDay, new DefaultQueueListener());
  }

  public void triggerNextEvent(BusSystem busModel, RailSystem railModel, int hourOfDay, QueueListener listener) {
    if (eventQueue.size() > 0) {
      SimEvent activeEvent = eventQueue.poll();
      lastRank = activeEvent.getRank();
      activeEvent.displayEvent();
      listener.currentRank(lastRank);
      switch (activeEvent.getType()) {
        case "move_bus":
          // identify the bus that will move
          Bus activeBus = busModel.getBus(activeEvent.getID());
          listener.printInformation(" the bus being observed is: " + Integer.toString(activeBus.getID()));

          // identify the current stop
          Route activeRoute = busModel.getRoute(activeBus.getRouteID());
          listener.printInformation(" the bus is driving on route: " + Integer.toString(activeRoute.getID()));

          int activeLocation = activeBus.getLocation();
          int activeStopID = activeRoute.getStopID(activeLocation);
          Stop activeStop = busModel.getStop(activeStopID);
          listener.printInformation(" the bus is currently at stop: " + Integer.toString(activeStop.getID()) + " - " + activeStop.getName());

          // drop off and pickup new passengers at current stop
          List<Rider> currentPassengers = activeBus.getPassengersForStop(activeStopID);

          List<Rider> passengerDifferential = activeStop.exchangeRiders(activeRoute.getID(), activeEvent.getRank(),
                  currentPassengers, activeBus
                  .getCapacity());
          listener.printInformation(" passengers pre-stop: " + Integer.toString(currentPassengers.size()) + " post-stop: " +
                  (currentPassengers.size() + passengerDifferential.size()));
          listener.printInformation(" passengers deboarding : "+
                  " " + (currentPassengers));
          listener.printInformation(" new passengers boarding : "+
                  " " + (passengerDifferential));
          activeBus.removeCurrentPassengers(currentPassengers);
          activeBus.addPassengerstoVehicle(passengerDifferential);
          busModel.addTransitPassengersToStop(currentPassengers,activeStopID,railModel);


          // determine next stop
          int nextLocation = activeRoute.getNextLocation(activeLocation);
          int nextStopID = activeRoute.getStopID(nextLocation);
          Stop nextStop = busModel.getStop(nextStopID);
          listener.printInformation(" the bus is heading to stop: " + Integer.toString(nextStopID) + " - " + nextStop.getName() + "\n");

          // find distance to stop to determine next event time
          Double travelDistance = activeStop.findDistance(nextStop);

          // conversion is used to translate time calculation from hours to minutes
          //int travelTime = 1 + (travelDistance.intValue() * 60 / activeBus.getSpeed());
          String sid = activeStop.getID().toString()+"-"+nextStop.getID().toString();
          long travelTime = busModel.getRoads(sid).getTravelTime(travelDistance,hourOfDay);
          activeBus.setLocation(nextLocation);

          // generate next event for this bus
          eventQueue.add(new SimEvent(activeEvent.getRank() + (int)travelTime , "move_bus", activeEvent.getID()));

          listener.reportVehicleMoved(activeBus);
          break;
        case "move_train":
          // identify the train that will move
          Train activeTrain = railModel.getTrain(activeEvent.getID());
          listener.printInformation(" the train being observed is: " + Integer.toString(activeTrain.getID()));

          // identify the current stop
          Route activeTrainRoute = railModel.getRoute(activeTrain.getRouteID());
          listener.printInformation(" the train is driving on route: " + Integer.toString(activeTrainRoute.getID()));

          int activeTrainLocation = activeTrain.getLocation();
          int activeTrainStopID = activeTrainRoute.getStopID(activeTrainLocation);
          Stop activeTrainStop = railModel.getStop(activeTrainStopID);
          listener.printInformation(" the train is currently at stop: " + Integer.toString(activeTrainStop.getID()) + " - " +
                  activeTrainStop.getName());

          // drop off and pickup new passengers at current stop
          List<Rider> currentTrainPassengers = activeTrain.getPassengersForStop(activeTrainStopID);
          List<Rider> trainPassengerDifferential = activeTrainStop.exchangeRiders(activeTrainRoute.getID(), activeEvent
                          .getRank(),
                  currentTrainPassengers, activeTrain
                          .getCapacity());
          listener.printInformation(" passengers pre-stop: " + Integer.toString(currentTrainPassengers.size()) + " post-stop: " +
                  (currentTrainPassengers.size() + trainPassengerDifferential.size()));
          listener.printInformation(" passengers deboarding : "+
                  " " + (currentTrainPassengers));
          listener.printInformation(" new passengers boarding : "+
                  " " + (trainPassengerDifferential));
          activeTrain.removeCurrentPassengers(currentTrainPassengers);
          activeTrain.addPassengerstoVehicle(trainPassengerDifferential);
          railModel.addTransitPassengersToStop(currentTrainPassengers,activeTrainStopID,busModel);


          // determine next stop
          int nextTrainLocation = activeTrainRoute.getNextLocation(activeTrainLocation);
          int nextTrainStopID = activeTrainRoute.getStopID(nextTrainLocation);
          Stop nextTrainStop = railModel.getStop(nextTrainStopID);
          listener.printInformation(" the train is heading to stop: " + Integer.toString(nextTrainStopID) + " - " +
                  nextTrainStop.getName() + "\n");



          // conversion is used to translate time calculation from hours to minutes
          activeTrain.setLocation(nextTrainLocation);

          //assuming a constant travel time of 4 mins
          int trainTravelTime = 4;

          // generate next event for this bus
          eventQueue.add(new SimEvent(activeEvent.getRank() + (int)trainTravelTime, "move_train", activeEvent.getID()));

          listener.reportVehicleMoved(activeTrain);
          break;
        default:
          listener.printInformation(" event not recognized");
          break;
      }
    } else {
      listener.printInformation(" event queue empty");
    }
  }

  public int getLastRank() { return lastRank; }

  public void addNewEvent(Integer eventRank, String eventType, Integer eventID) {
    eventQueue.add(new SimEvent(eventRank, eventType, eventID));
  }
}
