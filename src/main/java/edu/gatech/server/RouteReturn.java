package edu.gatech.server;

import edu.gatech.BusRoute;
import edu.gatech.RailRoute;
import edu.gatech.RoadInfo;
import edu.gatech.Route;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class RouteReturn {
  private int id;
  private int routeNumber;
  private String routeName;
  private int[] stopNumbers;
  private Map<Integer, RoadReturn> roads;

  RouteReturn(Route route) {
    this.id = route.getID();
    this.routeNumber = route.getNumber();
    this.routeName = route.getName();
    this.stopNumbers = new int[route.getLength()];
    for (int i = 0; i < route.getLength(); i++) {
      stopNumbers[i] = route.getStopID(i);
    }
    this.roads = new HashMap<>();
  }

  RouteReturn(BusRoute route, List<RoadInfo> roads) {
    this(route);
    Map<Integer, Integer> stopsToLocation = new HashMap<>();
    for (int i = 0; i < route.getLength(); i++) {
      int stopIDAtLocation = route.getStopID(i);
      stopsToLocation.put(i, stopIDAtLocation);
    }
    this.roads = new HashMap<>(route.getLength());
    for (int i = 0; i < roads.size(); i++) {
      RoadInfo info = roads.get(i);
      if (info != null && info.getRoadId() != null)
        this.roads.put(i, new RoadReturn(info));
      else
        this.roads.put(i, null);
    }
  }
}
