package edu.gatech.server;

import java.util.HashMap;
import java.util.Map;

class RouteCreation {
  public RouteCreation() {}

  private int routeNumber = 0;
  private String routeName = "";
  private int[] stopNumbers = new int[] {};
  private Map<String, RoadCreation> roads = new HashMap<String, RoadCreation>();

  public int getRouteNumber() { return routeNumber; }
  public String getRouteName() { return routeName; }
  public int[] getStopNumbers() { return stopNumbers; }
  public Map<String, RoadCreation> getRoads() { return roads; }
}
