package edu.gatech.server;

class VehicleCreation {
  public VehicleCreation() {}
  private int route = -1;
  private int location = -1;
  private int passengers = 0;
  private int capacity = 0;
  private int speed = 0;
  private boolean isMoving = false;
  public int getRoute() { return route; }
  public int getLocation() { return location; }
  public int getPassengers() { return passengers; }
  public int getCapacity() { return capacity; }
  public int getSpeed() { return speed; }
  public boolean isMoving() { return isMoving; }
}
