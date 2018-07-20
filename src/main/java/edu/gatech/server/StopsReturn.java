package edu.gatech.server;

import edu.gatech.BusStop;
import edu.gatech.Stop;

import java.util.Collection;

class StopsReturn {
  private StopReturn[] stops;

  StopsReturn(Collection<Stop> theBusStops) {
    this.stops = theBusStops.stream()
        .map(StopReturn::new)
        .toArray(StopReturn[]::new);
  }
}
