package edu.gatech.server;

import edu.gatech.Rider;

import java.util.List;

public class RiderReturn {
  private int id;
  private int beginStop;
  private int endStop;
  private int[] routeIDs;
  private int travelMode; // 0 = bus, 1 = rail, 2 = either/both

  public RiderReturn(Rider rider) {
    this(rider.getRiderId(), rider.getStartStopId(), rider.getDestStopId(), rider.getTransitStops(), rider.getTravelMode());
  }

  public RiderReturn(int id, int beginStop, int endStop, List<Integer> routeIDs, int travelMode) {
    this.id = id;
    this.beginStop = beginStop;
    this.endStop = endStop;
    this.routeIDs = new int[routeIDs.size()];
    for (int i = 0; i < routeIDs.size(); i++) {
      this.routeIDs[i] = routeIDs.get(i);
    }
    this.travelMode = travelMode;
  }
}
