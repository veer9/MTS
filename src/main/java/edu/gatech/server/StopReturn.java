package edu.gatech.server;

import edu.gatech.BusStop;
import edu.gatech.Stop;

class StopReturn {
  private int id;
  private double xCoord;
  private double yCoord;
  private int waiting;
  private String stopName;

  StopReturn(Stop busStop) {
    this.id = busStop.getID();
    this.xCoord = busStop.getXCoord();
    this.yCoord = busStop.getYCoord();
    this.waiting = busStop.getRidersAtStop().size();
    this.stopName = busStop.getName();
  }
}
