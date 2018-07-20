package edu.gatech.server;

import edu.gatech.Rider;

import java.util.List;
import java.util.stream.Collectors;

public class RidersReturn {
  private RiderReturn[] riders;

  public RidersReturn(List<Rider> riders) {
    this.riders = riders.stream().map(RiderReturn::new).toArray(RiderReturn[]::new);
  }
}
