package edu.gatech.server;

import edu.gatech.Vehicle;

import java.util.Collection;

public class VehiclesReturn {
  private VehicleReturn[] vehicles;

  VehiclesReturn(Collection<? extends Vehicle> vehicles) {
    this.vehicles = vehicles.stream()
        .map(VehicleReturn::new)
        .toArray(VehicleReturn[]::new);
  }

}
