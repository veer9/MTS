package edu.gatech.server;

import edu.gatech.Vehicle;

public class VehicleReturn {
  private int id = -1;
  private int route = -1;
  private int location = -1;
  private int previousLocation = -1;
  private int passengers = 0;
  private int capacity = 0;
  private int speed = 0;

  public VehicleReturn(Vehicle vehicle) {
    this.id = vehicle.getID();
    this.route = vehicle.getRouteID();
    this.location = vehicle.getLocation();
    this.previousLocation = vehicle.getPastLocation();
    this.capacity = vehicle.getCapacity();
    this.passengers = vehicle.getPassengers().size();
    this.speed = vehicle.getSpeed();
  }
}
