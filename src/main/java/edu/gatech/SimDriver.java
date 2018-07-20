package edu.gatech;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.sql.*;
import java.util.Properties;
import java.util.Set;

public class SimDriver {
  private SimQueue simEngine;
  private BusSystem martaModel;
  private RailSystem martaRail;
  private Random randGenerator;
  private static Integer HOUR_OF_DAY = 9;

  public SimDriver() {
    simEngine = new SimQueue();
    martaModel = new BusSystem();
    randGenerator = new Random();
    martaRail = new RailSystem();
  }

  public int getRank() {
    return simEngine.getLastRank();
  }

  public BusSystem getMartaBusSystem() {
    return martaModel;
  }

  public RailSystem getMartaRailSystem() {
    return martaRail;
  }

  public SimQueue getSimEngine() {
    return simEngine;
  }

  /*get an insight into all riders in the system and their current location*/
  public List<RiderDetails> getTotalRiders(){
    List<RiderDetails> rd = new ArrayList<RiderDetails>();
      for (Bus bus: martaModel.getBuses().values())
      {
        if (bus.getPassengers().size() > 0 ){
          RiderDetails rdetail = new RiderDetails();
          rdetail.setId(bus.getID());
          rdetail.setCurrentlocation("Bus");
          rdetail.setRiders(bus.getPassengers());
          rd.add(rdetail);
        }
      }
      for (Train train: martaRail.getTrains().values())
      {
        if (train.getPassengers().size() > 0 ) {
          RiderDetails rdetail = new RiderDetails();
          rdetail.setId(train.getID());
          rdetail.setCurrentlocation("Train");
          rdetail.setRiders(train.getPassengers());
          rd.add(rdetail);
        }
      }
      for (Stop stp: martaModel.getStops().values())
      {
        if (stp.getRidersAtStop().size() > 0 ) {
          RiderDetails rdetail = new RiderDetails();
          rdetail.setId(stp.getID());
          rdetail.setCurrentlocation("BusStop");
          rdetail.setRiders(stp.getRidersAtStop());
          rd.add(rdetail);
        }
      }
      for (Stop stp: martaRail.getStops().values()) {
        if (stp.getRidersAtStop().size() > 0) {
          RiderDetails rdetail = new RiderDetails();
          rdetail.setId(stp.getID());
          rdetail.setCurrentlocation("RailStop");
          rdetail.setRiders(stp.getRidersAtStop());
          rd.add(rdetail);
        }
      }
      return rd;

  }


  public void runInterpreter() {
    final String DELIMITER = ",";
    Scanner takeCommand = new Scanner(System.in);
    String[] tokens;

    do {
      String userCommandLine = takeCommand.nextLine();
      tokens = userCommandLine.split(DELIMITER);
      runCommand(tokens);
    } while (! tokens[0].equals("quit"));

    takeCommand.close();
  }

  public void runCommand(String[] tokens) {
    System.out.print("# main: ");

    switch (tokens[0]) {
      case "add_transit_info":
        martaModel.makeTransitInfo(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer
            .parseInt(tokens[3]), Integer.parseInt(tokens[4]));
        System.out.println(" new transit stop  created");
        break;
      case "get_transit_info":
        System.out.println("all transit stops "+martaModel.getAllTransitStopsInSystem());
        break;
      case "add_road_info":
        int roadId  = martaModel.makeRoadInfo(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer
            .parseInt(tokens[3]), Integer.parseInt(tokens[4]),Integer.parseInt(tokens[5]),Integer.parseInt(tokens[6]),tokens[7]);
        System.out.println(" new road info: " + Integer.toString(roadId) + " added");
        break;
      case "add_rider":
        int riderId = martaModel.makeRider(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer
            .parseInt(tokens[3]),tokens[4],Integer.parseInt(tokens[5]));
        System.out.println(" new rider: " + Integer.toString(riderId) + " created");
        break;
      case "add_rail_event":
        simEngine.addNewEvent(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
        System.out.print(" new event - rank: " + Integer.parseInt(tokens[1]));
        System.out.println(" type: " + tokens[2] + " ID: " + Integer.parseInt(tokens[3]) + " created");
        break;
      case "add_rail_stop":
        int tstopID = martaRail.makeStop(Integer.parseInt(tokens[1]), tokens[2]);
        System.out.println(" new stop: " + Integer.toString(tstopID) + " created");
        break;
      case "add_rail_route":
        int trouteID = martaRail.makeRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
        System.out.println(" new route: " + Integer.toString(trouteID) + " created");
        break;
      case "add_train":
        int tID = martaRail.makeTrain(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt
            (tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
        System.out.println(" new bus: " + Integer.toString(tID) + " created");
        break;
      case "extend_rail_route":
        martaRail.appendStopToRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        System.out.println(" stop: " + Integer.parseInt(tokens[2]) + " appended to route " + Integer.parseInt(tokens[1]));
        break;
      case "add_bus_event":
        simEngine.addNewEvent(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
        System.out.print(" new event - rank: " + Integer.parseInt(tokens[1]));
        System.out.println(" type: " + tokens[2] + " ID: " + Integer.parseInt(tokens[3]) + " created");
        break;
      case "add_bus_stop":
        int stopID = martaModel.makeStop(Integer.parseInt(tokens[1]), tokens[2], Integer.parseInt(tokens[3]), Double.parseDouble(tokens[4]));
        System.out.println(" new stop: " + Integer.toString(stopID) + " created");
        break;
      case "add_bus_route":
        int routeID = martaModel.makeRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
        System.out.println(" new route: " + Integer.toString(routeID) + " created");
        break;
      case "add_bus":
        int busID = martaModel.makeBus(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
        System.out.println(" new bus: " + Integer.toString(busID) + " created");
        break;
      case "extend_bus_route":
        martaModel.appendStopToRoute(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        martaModel.appendRouteToStop(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        System.out.println(" stop: " + Integer.parseInt(tokens[2]) + " appended to route " + Integer.parseInt(tokens[1]));
        break;
      case "upload_real_data":
        uploadMARTAData();
        break;
      case "step_once":
        simEngine.triggerNextEvent(martaModel,martaRail,HOUR_OF_DAY);
        System.out.println(" queue activated for 1 event");
        break;
      case "step_multi":
        System.out.println(" queue activated for " + Integer.parseInt(tokens[1]) + " event(s)");
        for (int i = 0; i < Integer.parseInt(tokens[1]); i++) {
          // display the number of events completed for a given frequency
          if (tokens.length >= 3) {
            if (i % Integer.parseInt(tokens[2]) == 0) { System.out.println("> " + Integer.toString(i) + " events completed"); }
          }

          // execute the next event
          simEngine.triggerNextEvent(martaModel,martaRail,HOUR_OF_DAY);

          // pause after each event for a given number of seconds
          if (tokens.length >= 4) {
            try { Thread.sleep(Integer.parseInt(tokens[3]) * 1000); }
            catch (InterruptedException e) { e.printStackTrace(); }
          }
          // regenerate the model display (Graphviz dot file) for a given frequency
          if (tokens.length >= 5) {
            if (i % Integer.parseInt(tokens[4]) == 0) { martaModel.displayModel();}
          }
        }
        break;
      case "system_report":
        System.out.println(" system report - stops, buses and routes:");
        for (Stop singleStop: martaModel.getStops().values()) { singleStop.displayInternalStatus(); }
        for (Bus singleBus: martaModel.getBuses().values()) { singleBus.displayInternalStatus(); }
        for (Route singleRoute: martaModel.getRoutes().values()) { singleRoute.displayInternalStatus(); }
        for (Stop singleStop: martaRail.getStops().values()) { singleStop.displayInternalStatus(); }
        for (Train singleTrain: martaRail.getTrains().values()) { singleTrain.displayInternalStatus(); }
        for (Route singleRoute: martaRail.getRoutes().values()) { singleRoute.displayInternalStatus(); }
        break;
      case "display_event_model":
        for (SimEvent simEvent: simEngine.getListOfActiveEvents()) { simEvent.displayEvent(); }
        //martaModel.displayModel();
        break;
      case "riders_in_system":
        System.out.println(getTotalRiders());
        break;
      case "road_info_stops":
        System.out.println(martaModel.getRoadInfoBetweenStops(Integer.parseInt(tokens[1]), Integer.parseInt
            (tokens[2])));
        break;
      case "get_all_roads":
        System.out.println(martaModel.getAllRoadInfo());
        break;
      case "add_ranked_event":
        simEngine.addNewEvent(Integer.parseInt(tokens[1]), tokens[2],Integer.parseInt
            (tokens[3]));
        break;
      case "remove_ranked_event":
        simEngine.removeEvent(Integer.parseInt(tokens[1]), tokens[2],Integer.parseInt
            (tokens[3]));
        break;
      case "quit":
        System.out.println(" stop the command loop");
        break;
      default:
        System.out.println(" command not recognized");
        break;
    }

  }

  public void uploadMARTAData() {
    ResultSet rs;
    int recordCounter;

    Integer stopID, routeID;
    String stopName, routeName;
    // String direction;
    Double latitude, longitude;

    // intermediate data structures needed for assembling the routes
    HashMap<Integer, ArrayList<Integer>> routeLists = new HashMap<Integer, ArrayList<Integer>>();
    ArrayList<Integer> targetList;
    ArrayList<Integer> circularRouteList = new ArrayList<Integer>();

    try {
      // connect to the local database system
      System.out.println(" connecting to the database");
      String url = "jdbc:postgresql://localhost:5432/martadb";
      Properties props = new Properties();
      props.setProperty("user", "postgres");
      props.setProperty("password", "cs6310");
      //props.setProperty("ssl", "true");

      Connection conn = DriverManager.getConnection(url, props);
      Statement stmt = conn.createStatement();

      // create the stops
      System.out.print(" extracting and adding the stops: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM apcdata_stops");
      while (rs.next()) {
        stopID = rs.getInt("min_stop_id");
        stopName = rs.getString("stop_name");
        latitude = rs.getDouble("latitude");
        longitude = rs.getDouble("longitude");

        martaModel.makeStop(stopID,stopName,latitude,longitude);
        recordCounter++;
      }
      System.out.println(Integer.toString(recordCounter) + " added");

      // create the routes
      System.out.print(" extracting and adding the routes: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM apcdata_routes");
      while (rs.next()) {
        routeID = rs.getInt("route");
        routeName = rs.getString("route_name");

        martaModel.makeRoute(routeID, routeID, routeName);
        recordCounter++;

        // initialize the list of stops for the route as needed
        routeLists.putIfAbsent(routeID, new ArrayList<Integer>());
      }
      System.out.println(Integer.toString(recordCounter) + " added");

      // add the stops to all of the routes
      System.out.print(" extracting and assigning stops to the routes: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM apcdata_routelist_oneway");
      while (rs.next()) {
        routeID = rs.getInt("route");
        stopID = rs.getInt("min_stop_id");
        // direction = rs.getString("direction");

        targetList = routeLists.get(routeID);
        if (!targetList.contains(stopID)) {
          martaModel.appendStopToRoute(routeID, stopID);
          martaModel.appendRouteToStop(routeID, stopID);
          recordCounter++;
          targetList.add(stopID);
      
        }
      }

      // add the reverse "route back home" stops for two-way routes
      for (Integer reverseRouteID : routeLists.keySet()) {
        if (!circularRouteList.contains(reverseRouteID)) {
          targetList = routeLists.get(reverseRouteID);
          for (int i = targetList.size() - 1; i > 0; i--) {
            martaModel.appendStopToRoute(reverseRouteID, targetList.get(i));
            martaModel.appendRouteToStop(reverseRouteID, targetList.get(i));
          }
        }
      }
      System.out.println(Integer.toString(recordCounter) + " assigned");

      // create the buses and related event(s)
      System.out.print(" extracting and adding the buses and events: ");
      recordCounter = 0;
      int busID = 0;
      rs = stmt.executeQuery("SELECT * FROM apcdata_bus_distributions");
      while (rs.next()) {
        routeID = rs.getInt("route");
        int minBuses = rs.getInt("min_buses");
        int avgBuses  = rs.getInt("avg_buses");
        int maxBuses = rs.getInt("max_buses");

        int routeLength = martaModel.getRoute(routeID).getLength();
        int suggestedBuses = randomBiasedValue(minBuses, avgBuses, maxBuses);
        int busesOnRoute = Math.max(1, Math.min(routeLength / 2, suggestedBuses));

        int startingPosition = 0;
        int skip = Math.max(1, routeLength / busesOnRoute);
        for (int i = 0; i < busesOnRoute; i++) {
          martaModel.makeBus(busID, routeID, startingPosition + i * skip,  10, 1);
          simEngine.addNewEvent(0,"move_bus", busID++);
          recordCounter++;
        }
      }
      System.out.println(Integer.toString(recordCounter) + " added");

      // create transit info

      System.out.print(" extracting and adding transit info: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM transit_info");
      while (rs.next()) {

        int railRoute = rs.getInt("rail_route");
        int railStop = rs.getInt("rail_stop");
        int busRoute  = rs.getInt("bus_route");
        int busStop = rs.getInt("bus_stop");

        TransitStop tInfo = new TransitStop(railRoute,railStop,busRoute,busStop);

        martaModel.addTransitStops(tInfo);
        recordCounter++;
      }
      System.out.println(Integer.toString(recordCounter) + " added");

      // create the rider-passenger generator and associated event(s)

      System.out.print(" extracting and adding the riders: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM rider_distribution");
      while (rs.next()) {

        //String routes = rs.getString("route");
        int riderId = rs.getInt("rider_id");
        stopID  = rs.getInt("start_stop");
        int destStop = rs.getInt("dest_stop");
        // 0-Busonly,1-Trainonly,2-BusandTrain- Defaults to bus only
        int travelMode = rs.getInt("travel_mode");

        List<Integer> list = Arrays.asList((Integer[])rs.getArray("route").getArray());
        Rider rider = new Rider();
        rider.setDestStopId(destStop);
        rider.setStartStopId(stopID);
        rider.setRoutes(list);
        rider.setRiderId(riderId);
        rider.setTransitCount(0);
        rider.setTravelMode(travelMode);
        TransitSystem model = martaModel;
        if (travelMode == 1) {
          model = martaRail;
        }
        if (list.size() > 1 ) {
          List<Integer> ts = model.findTransitStopDetails(list,travelMode);
          rider.setTransitStops(ts);
        }
        Stop stop = model.getStop(stopID);
        // Some of our data is a little messy, this guard is needed until
        // we clean it up.
        if (stop != null && rider.getTransitStops() != null) {
          stop.addNewRiders(rider);
          recordCounter++;
        }
      }
      System.out.println(Integer.toString(recordCounter) + " added");


      // create road info

      System.out.print(" extracting and adding road info: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM road_info");
      while (rs.next()) {

        int routeId = rs.getInt("route_id");
        String traffic = rs.getString("traffic");
        int roadId = rs.getInt("road_id");
        int stop1  = rs.getInt("stop1");
        int stop2 = rs.getInt("stop2");
        int speedLimit = rs.getInt("speed_limit");
        int roadWork = rs.getInt("road_work");

        RoadInfo road = new RoadInfo( roadId,  stop1,  stop2,
                 speedLimit,  routeId, roadWork,  traffic);

        martaModel.addRoadInfo(road);
        recordCounter++;
      }
      System.out.println(Integer.toString(recordCounter) + " added");


      // create rail info

      System.out.print(" extracting and adding rail data: ");
      recordCounter = 0;
      rs = stmt.executeQuery("SELECT * FROM train_data");
      while (rs.next()) {

        int trainId = rs.getInt("train_id");
        String routName = rs.getString("route_name");
        int routeId = rs.getInt("route_id");
        int stopId  = rs.getInt("stop_id");
        String stpName = rs.getString("stop_name");
        String rtDir = rs.getString("route_direction");

        Train trainObj = new Train(trainId,routeId);
        // train.add(trainObj);
        martaRail.getTrains().put(trainId,trainObj);
        simEngine.addNewEvent(0,"move_train", trainId);

        RailStop rstop = new RailStop(stopId,stpName);
        //stop.add(rstop);

        martaRail.getStops().put(stopId,rstop);

        Route route = martaRail.getRoute(routeId);
        if (route == null)  {
          route = new RailRoute(routeId,routeId,routName);
          martaRail.getRoutes().put(routeId,route);
        }
        martaRail.appendStopToRoute(routeId,stopId);
        martaRail.appendRouteToStop(routeId,stopId);
        recordCounter++;
      }

      System.out.println(Integer.toString(recordCounter) + " added");

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Discovered exception: ");
      System.err.println(e.getMessage());
    }
  }



  private int randomBiasedValue(int lower, int middle, int upper) {
    int lowerRange = randGenerator.nextInt(middle - lower + 1) + lower;
    int upperRange = randGenerator.nextInt(upper - middle + 1) + middle;
    return (lowerRange + upperRange) /2;
  }

}
