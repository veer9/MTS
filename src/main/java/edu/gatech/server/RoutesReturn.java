package edu.gatech.server;

import edu.gatech.Route;

import java.util.Collection;
import java.util.stream.Collectors;

public class RoutesReturn {
  private RouteReturn[] routes;

  static RoutesReturn fromRoutes(Collection<Route> routes) {
    Collection<RouteReturn> routeReturns =
        routes.stream().map(RouteReturn::new).collect(Collectors.toList());
    return new RoutesReturn(routeReturns);
  }

  RoutesReturn(Collection<RouteReturn> routeReturns) {
    this.routes = routeReturns.toArray(new RouteReturn[0]);
  }
}
