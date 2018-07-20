package edu.gatech.server;

public class RoadCreation {
  public int getBeginsAt() {
    return beginsAt;
  }

  public int getEndsAt() {
    return endsAt;
  }

  public int getSpeedLimit() {
    return speedLimit;
  }

  public int getRoadWork() {
    return roadWork;
  }

  public String getTraffic() {
    return traffic;
  }

  private int beginsAt;
  private int endsAt;
  private int speedLimit;
  private int roadWork;
  private String traffic;

  public RoadCreation() {}


}
