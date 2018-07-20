package edu.gatech.server;

import edu.gatech.BusSystem;
import edu.gatech.RailSystem;
import edu.gatech.SimQueue;

public class StatusReturn {
  private int currentRank;
  private int busStopsCount;
  private int railStopsCount;
  private int busRoutesCount;
  private int railRoutesCount;
  private int busesCount;
  private int trainsCount;

  StatusReturn(SimQueue queue, BusSystem busSystem, RailSystem railSystem) {
    this.currentRank = queue.getLastRank();
    this.busesCount = busSystem.getBuses().size();
    this.busRoutesCount = busSystem.getRoutes().size();
    this.busStopsCount = busSystem.getStops().size();
    this.trainsCount = railSystem.getTrains().size();
    this.railRoutesCount = railSystem.getRoutes().size();
    this.railStopsCount = railSystem.getStops().size();
  }
}
