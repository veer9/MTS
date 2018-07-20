package edu.gatech.server;

import edu.gatech.RoadInfo;

public class RoadReturn {
  public int getBeginsAt() {
    return beginsAt;
  }

  public int getEndsAt() {
    return endsAt;
  }

  private int beginsAt;
  private int endsAt;
  private int speedLimit;
  private int roadWork;
  private String traffic;

  public RoadReturn(RoadInfo road){
    this.beginsAt = road.getStop1();
    this.endsAt = road.getStop2();
    this.roadWork = road.getRoadWork();
    this.traffic = road.getTraffic();
    this.speedLimit = road.getSpeedLimit();
  }
}
