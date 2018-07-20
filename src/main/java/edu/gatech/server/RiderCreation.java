package edu.gatech.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RiderCreation {
  private int beginStop;
  private int endStop;
  private int[] routeIDs;
  private int travelMode; // 0 = bus, 1 = rail, 2 = either/both

  public int getBeginStop() {
    return beginStop;
  }

  public int getEndStop() {
    return endStop;
  }

  public int[] getRouteIDs() {
    return routeIDs;
  }

  public List<Integer> getRoutes() {
    return Arrays.stream(routeIDs)
        .boxed()
        .collect(Collectors.toList());
  }

  public int getTravelMode() {
    return travelMode;
  }
}
