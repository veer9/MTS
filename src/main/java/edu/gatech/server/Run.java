package edu.gatech.server;

import com.google.gson.Gson;
import edu.gatech.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;

import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Run {
  static {
    java.lang.System.setProperty("org.slf4j.simpleLogger.log.edu.gatech.server.Run", "warn");
  }

  static SimDriver commandInterpreter = new SimDriver();

  static Gson gson = new Gson();

  static Logger logger = LoggerFactory.getLogger("edu.gatech.server.Run");

  static SimRunner simRunner = new SimRunner(commandInterpreter);
  static Thread runThread = new Thread(simRunner);

  static {
    runThread.start();
  }

  public static void main(String[] args) {
    staticFiles.location("/edu/gatech/server");

    createDummyData();

    MustacheTemplateEngine templateEngine = new MustacheTemplateEngine("edu/gatech/server");

    before((request, response) -> {
      URL url = new URL(request.url());
      if (! simRunner.isIdle() && !url.getPath().contains("/run")) {
        halt(503, "Currently processing");
      } else {
        request.attribute("commandInterpreter", Run.commandInterpreter);
      }
    });

    get("/", "text/html", (request, response) -> {
      Map<String, Object> model = new HashMap<>();
      return new ModelAndView(model, "index.html.mustache");
    }, templateEngine);

    post("/run", (request, response) -> {
      Optional<RunCreation> run = parseRequest(request, RunCreation.class, "run");
      if (run.isPresent()) {
        boolean isRunning = simRunner.runFor(run.get().getStepsToRun());
        if (isRunning) {
          response.header("Content-Type", "application/json");
          return gson.toJson(new RunReturn("in_progress"));
        } else {
          return gson.toJson(simRunner.getStatus());
        }
      } else {
        return handleFailure("run", response);
      }
    });

    get("/run", "application/json", (request, response) -> {
      response.header("Content-Type", "application/json");
      return gson.toJson(simRunner.getStatus());
    });

    post("/load", "application/json", (request, response) -> {
      response.header("Content-Type", "application/json");
      boolean willLoad = simRunner.load();
      if (willLoad) {
        return gson.toJson(new RunReturn("loading"));
      } else {
        response.status(503);
        return gson.toJson(new RunReturn("retry"));
      }
    });

    post("/reset", (request, response) -> {
      response.header("Content-Type", "application/json");
      boolean isStopping = simRunner.stop();
      if (isStopping) {
        commandInterpreter = new SimDriver();
        simRunner = new SimRunner(commandInterpreter);
        runThread = new Thread(simRunner);
        runThread.start();
        return gson.toJson(new RunReturn("reset"));
      } else {
        return gson.toJson(new RunReturn("operation_in_progress"));
      }
    });

    get("/status", "application/json", (request, response) -> {
      response.header("Content-Type", "application/json");
      return gson.toJson(
          new StatusReturn(
              getCommandInterpreter(request).getSimEngine(),
              getCommandInterpreter(request).getMartaBusSystem(),
              getCommandInterpreter(request).getMartaRailSystem()));
    });

    get("/bus/stops.html", "text/html", (request, response) -> {
      return new ModelAndView(
          new ListResourcesModel("/bus/stops", "stops", "/create_stop.html?type=bus"),
         "list.html.mustache");
    }, templateEngine);

    post("/bus/stops", generateStopCreationHandler("bus stop", SimDriver::getMartaBusSystem, Run::createBusStop));

    get("/bus/stops", Run.generateListHandler((model) -> model.getMartaBusSystem().getStops().values(), StopsReturn::new));

    get("/bus/stops/:id", generateSingleHandler((model, id) -> model.getMartaBusSystem().getStop(id), StopReturn::new));

    post("/bus/riders", generateRiderCreationHandler(SimDriver::getMartaBusSystem, 0));

    get("/bus/riders", "application/json",
        Run.generateListRidersRoute(SimDriver::getMartaBusSystem, (busSytem) -> busSytem.getBuses().values()));

    get("/rail/stops.html", "text/html", (request, response) -> {
      return new ModelAndView(
          new ListResourcesModel("/rail/stops", "stops", "/create_stop.html?type=rail"),
         "list.html.mustache");
    }, templateEngine);

    post("/rail/stops", generateStopCreationHandler("rail stop", SimDriver::getMartaRailSystem, Run::createRailStop));

    get("/rail/stops", Run.generateListHandler((model) -> model.getMartaRailSystem().getStops().values(), StopsReturn::new));

    get("/rail/stops/:id", Run.generateSingleHandler((model, id) -> model.getMartaRailSystem().getStop(id), StopReturn::new));

    post("/rail/riders", generateRiderCreationHandler(SimDriver::getMartaRailSystem, 1));

    get("/rail/riders", "application/json",
        Run.generateListRidersRoute(SimDriver::getMartaRailSystem, (railSytem) -> railSytem.getTrains().values()));

    get("/bus/routes.html", "text/html",
        routeListView("linkBusStopNumbers", "bus"),
        templateEngine);

    post("/bus/routes", generateRouteCreationHandler(SimDriver::getMartaBusSystem, Run::createBusRoute));

    get("/bus/routes", Run.generateListHandler(
        (model) -> model.getMartaBusSystem().getRoutes().values(),
        (routes, commandInterpreter) -> new RoutesReturn(routes.stream()
            .map((route) -> { return Run.buildRouteReturn((edu.gatech.Route) route, commandInterpreter); })
            .collect(Collectors.toList()))));

    get("/bus/routes/:id", generateSingleHandler(
        (model, id) -> model.getMartaBusSystem().getRoute(id),
        Run::buildRouteReturn));

    get("/bus/road", (request, response) -> {
      response.header("Content-Type", "application/json");
      String fromId = request.queryParamOrDefault("from", "-1");
      String toId = request.queryParamOrDefault("to", "1");
      RoadInfo roadInfo = getCommandInterpreter(request).getMartaBusSystem().getRoads(fromId + "-" + toId);
      if (roadInfo != null && roadInfo.getRoadId() != null) {
        return gson.toJson(new RoadReturn(roadInfo));
      } else {
        response.status(404);
        return "{\"error\" : \"road not found\"}";
      }
    });

    get("/rail/routes.html", "text/html",
        routeListView("linkRailStopNumbers", "rail"),
        templateEngine);

    post("/rail/routes", generateRouteCreationHandler(SimDriver::getMartaRailSystem, Run::createRailRoute));

    get("/rail/routes", Run.generateListHandler((model) -> model.getMartaRailSystem().getRoutes().values(),
        RoutesReturn::fromRoutes));

    get("/rail/routes/:id",
        generateSingleHandler((model, id) -> model.getMartaRailSystem().getRoute(id),
            (Function<edu.gatech.Route, RouteReturn>) RouteReturn::new));

    get("/bus/vehicles.html",
        "text/html",
        getVehiclesList("linkBusRouteNumber", "bus"),
        templateEngine);

    post("/bus/vehicles", generateVehicleCreationHandler(
            "bus",
            SimDriver::getMartaBusSystem,
            Run::createNewBus,
            BusSystem::getBus));

    get("/bus/vehicles", Run.generateListHandler((model) -> model.getMartaBusSystem().getBuses().values(), VehiclesReturn::new));

    get("/bus/vehicles/:id", generateSingleHandler((model, id) -> model.getMartaBusSystem().getBus(id), VehicleReturn::new));

    get("/rail/vehicles.html",
        "text/html",
        getVehiclesList("linkRailRouteNumber",
        "rail"), templateEngine);

    post("/rail/vehicles", generateVehicleCreationHandler(
        "train",
        SimDriver::getMartaRailSystem,
        Run::createNewTrain,
        RailSystem::getTrain));

    get("/rail/vehicles", Run.generateListHandler((model) -> model.getMartaRailSystem().getTrains().values(), VehiclesReturn::new));

    get("/rail/vehicles/:id", generateSingleHandler((model, id) -> model.getMartaRailSystem().getTrain(id), VehicleReturn::new));
  }

  private static SimDriver getCommandInterpreter(Request request) {
    return (SimDriver) request.attribute("commandInterpreter");
  }

  private static RouteReturn buildRouteReturn(edu.gatech.Route route, SimDriver commandInterpreter) {
    if (route instanceof BusRoute) {
      BusRoute busRoute = (BusRoute) route;
      return new RouteReturn(busRoute,
          busRoute.getRoadKeys()
              .stream()
              .map((roadKey) -> commandInterpreter.getMartaBusSystem().getRoads(roadKey))
              .collect(Collectors.toList())
      );
    } else {
      return new RouteReturn(route);
    }
  }

  private static <S extends TransitSystem> Route generateVehicleCreationHandler(
      String vehicleName,
      Function<SimDriver, S> getSystem,
      BiFunction<VehicleCreation, S, Integer> createVehicle,
      BiFunction<S, Integer, Vehicle> getNewlyCreatedVehicle
      ) {
    return (request, response) -> {
      Optional<VehicleCreation> bc = parseRequest(request, VehicleCreation.class, vehicleName);
      if (bc.isPresent()) {
        VehicleCreation vehicle = bc.get();
        S model = getSystem.apply(commandInterpreter);
        int newVehicleId = createVehicle.apply(vehicle, model);
        if (vehicle.isMoving()) {
          getCommandInterpreter(request).getSimEngine().addNewEvent(getCommandInterpreter(request).getRank(), "move_" + vehicleName, newVehicleId);
        }
        response.header("Content-Type", "application/json");
        return gson.toJson(getNewlyCreatedVehicle.apply(model, newVehicleId));
      } else {
        return handleFailure(vehicleName, response);
      }
    };
  }

  private static Integer createNewTrain(VehicleCreation train, RailSystem model) {
    int id = model.getTrains().size();
    return model.makeTrain(id, train.getRoute(), train.getLocation(), train.getCapacity(), train.getSpeed());
  }

  private static Integer createNewBus(VehicleCreation train, BusSystem model) {
    int id = model.getBuses().size();
    return model.makeBus(id, train.getRoute(), train.getLocation(), train.getCapacity(), train.getSpeed());
  }

  private static TemplateViewRoute getVehiclesList(String routeNameLookup, String stem) {
    return (request, response) -> {
      Map<String, String> overrides = new HashMap<String, String>();
      overrides.put("route", routeNameLookup);
      ListResourcesModel resources =
          new ListResourcesModel(
              "/" + stem + "/vehicles",
              "vehicles",
              "/create_vehicle.html?type=" + stem,
              overrides);
      return new ModelAndView(resources, "list.html.mustache");
    };
  }

  private static TemplateViewRoute routeListView(String linkStopNumbers, String stem) {
    return (request, response) -> {
      Map<String, String> overrides = new HashMap<String, String>();
      overrides.put("stopNumbers", linkStopNumbers);
      return new ModelAndView(
          new ListResourcesModel("/" + stem + "/routes", "routes", "/create_route.html?type=" + stem, overrides),
          "list.html.mustache");
    };
  }

  static private <S extends TransitSystem> Route generateStopCreationHandler(
      String typeName,
      Function<SimDriver, S> getSystem,
      BiFunction<StopCreation, S, Integer> createNew) {
    return (request, response) -> {
      Optional<StopCreation> sc = parseRequest(request, StopCreation.class, typeName);
      if (sc.isPresent()) {
        S model = getSystem.apply(getCommandInterpreter(request));
        Integer newStopId = createNew.apply(sc.get(), model);
        response.header("Content-Type", "application/json");
        return gson.toJson(new StopReturn(model.getStop(newStopId)));
      } else {
        return handleFailure("route", response);
      }
    };
  }

  private static <S extends TransitSystem> Route generateRouteCreationHandler(
      Function<SimDriver, S> getSystem,
      BiFunction<RouteCreation, S, Integer> createNew
  ) {
    return (request, response) -> {
      response.header("Content-Type", "application/json");
      System.err.println(request.body());
      Optional<RouteCreation> rc = parseRequest(request, RouteCreation.class, "route");
      if (rc.isPresent()) {
        RouteCreation route = rc.get();
        S model = getSystem.apply(getCommandInterpreter(request));
        int newRouteId = createNew.apply(route, model);
        Arrays.stream(route.getStopNumbers())
            .forEach((stopId) -> {
              model.appendStopToRoute(newRouteId, stopId);
              model.appendRouteToStop(newRouteId, stopId);
            });
        response.header("Content-Type", "application/json");
        return gson.toJson(new RouteReturn(model.getRoute(newRouteId)));
      } else {
        return handleFailure("route", response);
      }
    };
  }

  private static <S extends TransitSystem> Route generateRiderCreationHandler(
      Function<SimDriver, S> getSystem,
      int acceptableTravelMode) {
    return ((request, response) -> {
      Optional<RiderCreation> riderReq = parseRequest(request, RiderCreation.class, "rider");
      if (riderReq.isPresent()) {
        RiderCreation rider = riderReq.get();
        SimDriver commandInterpreter = (SimDriver) request.attribute("commandInterpreter");
        TransitSystem transitSystem = getSystem.apply(commandInterpreter);
        int id = transitSystem.getLastRiderId() + 1;
        if (transitSystem.getStop(rider.getBeginStop()) != null &&
            transitSystem.getStop(rider.getEndStop()) != null &&
            rider.getRoutes().stream().allMatch((routeId) -> transitSystem.getRoute(routeId) != null) &&
            (rider.getTravelMode() == acceptableTravelMode || rider.getTravelMode() == 2)) {
          int riderId =
              transitSystem.makeRider(id, rider.getBeginStop(), rider.getEndStop(), rider.getRoutes(), rider.getTravelMode());
          List<Rider> stopRiders = transitSystem.getStop(rider.getBeginStop()).getRidersAtStop();
          Optional<RiderReturn> createdRider =
              stopRiders.stream().filter(r -> r.getRiderId() == riderId).findFirst().map(RiderReturn::new);
          if (createdRider.isPresent()) {
            response.header("Content-Type", "application/json");
            return gson.toJson(createdRider.get());
          } else {
            return handleFailure("run (internal error)", response);
          }
        } else {
          return handleFailure("run (invalid)", response);
        }
      } else {
        return handleFailure("run", response);
      }
    });
  }

  private static <S extends TransitSystem, T extends Vehicle> Route generateListRidersRoute(
      Function<SimDriver, S> getSystem,
      Function<S, Collection<T>> getVehicles) {
    return ((request, response) -> {
      SimDriver commandInterpreter = (SimDriver) request.attribute("commandInterpreter");
      S system = getSystem.apply(commandInterpreter);
      Collection<Stop> allStops = system.getStops().values();
      Collection<T> allVehicles = getVehicles.apply(system);
      List<Rider> allRiders =
          allStops.stream()
              .flatMap(stop -> stop.getRidersAtStop().stream())
              .collect(Collectors.toList());
      allRiders.addAll(
          allVehicles
              .stream()
              .flatMap(vehicle -> vehicle.getPassengers().stream())
              .collect(Collectors.toList()));
      response.header("Content-Type", "application/json");
      return gson.toJson(new RidersReturn(allRiders));
    });
  }

  private static Integer createBusRoute(RouteCreation route, BusSystem martaModel) {
    int nextRouteId = martaModel.getRoutes().size();
    int newRouteId = martaModel.makeRoute(nextRouteId, route.getRouteNumber(), route.getRouteName());
    int uniqueRoadId = martaModel.getAllRoadInfo().size();
    int[] stops = route.getStopNumbers();
    for (String roadKey: route.getRoads().keySet()) {
      RoadCreation road = route.getRoads().get(roadKey);
      martaModel.makeRoadInfo(uniqueRoadId,
          route.getRouteNumber(),
          road.getBeginsAt(),
          road.getEndsAt(),
          road.getRoadWork(),
          road.getSpeedLimit(),
          road.getTraffic());
      uniqueRoadId++;
    }
    return newRouteId;
  }

  private static Integer createRailRoute(RouteCreation route, RailSystem martaModel) {
    int nextRouteId = martaModel.getRoutes().size();
    return martaModel.makeRoute(nextRouteId, route.getRouteNumber(), route.getRouteName());
  }

  private static Integer createRailStop(StopCreation railStop, RailSystem martaModel) {
    int nextStopId = martaModel.getStops().size();
    return martaModel.makeStop(nextStopId, railStop.getName());
  }

  private static Integer createBusStop(StopCreation busStop, BusSystem martaModel) {
    int nextStopId = martaModel.getStops().size();
    return martaModel.makeStop(nextStopId, busStop.getName(), busStop.getXCoord(), busStop.getYCoord());
  }

  static private <T, R> Route generateListHandler(
      Function<SimDriver, Collection<T>> getList,
      Function<Collection<T>, R> toGsonable) {
    return (request, response) -> {
      R gsonable = toGsonable.apply(getList.apply(getCommandInterpreter(request)));
      response.header("Content-Type", "application/json");
      return gson.toJson(gsonable);
    };
  }

  static private <T, R> Route generateListHandler(
      Function<SimDriver, Collection<T>> getList,
      BiFunction<Collection<T>, SimDriver, R> toGsonable) {
    return (request, response) -> {
      R gsonable = toGsonable.apply(getList.apply(getCommandInterpreter(request)), getCommandInterpreter(request));
      response.header("Content-Type", "application/json");
      return gson.toJson(gsonable);
    };
  }

  static private <T, R> Route generateSingleHandler(
      BiFunction<SimDriver, Integer, T> getItem,
      Function<T, R> toGsonable) {
    return (request, response) -> {
      int id = Integer.parseInt(request.params(":id"));
      R gsonable = toGsonable.apply(getItem.apply(getCommandInterpreter(request), id));
      response.header("Content-Type", "application/json");
      return gson.toJson(gsonable);
    };
  }

  static private <T, R> Route generateSingleHandler(
      BiFunction<SimDriver, Integer, T> getItem,
      BiFunction<T, SimDriver, R> toGsonable) {
    return (request, response) -> {
      int id = Integer.parseInt(request.params(":id"));
      R gsonable = toGsonable.apply(getItem.apply(getCommandInterpreter(request), id), getCommandInterpreter(request));
      response.header("Content-Type", "application/json");
      return gson.toJson(gsonable);
    };
  }

  static private String handleFailure(String typeName, Response response) {
    logger.debug(typeName + " creation error");
    response.status(422);
    response.header("Content-Type", "application/json");
    return gson.toJson(new ErrorReturn("bad " + typeName));
  }

  static private <T> Optional<T> parseRequest(Request request, Class<T> klass, String typeName) {
    String bodyText = request.body();
    logger.debug("received " + typeName + ":", bodyText);
    T parsed = gson.fromJson(bodyText, klass);
    return Optional.ofNullable(parsed);
  }

  static private void createDummyData() {
    BusSystem busSystem = commandInterpreter.getMartaBusSystem();
    int stopOne = busSystem.makeStop(0, "Sandy Springs", 5, 10);
    int stopTwo = busSystem.makeStop(1, "Dunwoody", 5, 9);
    int stopThree = busSystem.makeStop(2, "Medical Center", 10, 5);
    int routeID = busSystem.makeRoute(0, 8, "North");
    int roadOne = busSystem.makeRoadInfo(0, 0, 0, 1,0,25, "Heavy");
    int roadTwo = busSystem.makeRoadInfo(0, 0, 1, 0,0,25, "Normal");
    busSystem.appendStopToRoute(routeID, stopOne);
    busSystem.appendRouteToStop(stopOne, routeID);
    busSystem.appendStopToRoute(routeID, stopTwo);
    busSystem.appendRouteToStop(stopTwo, routeID);
    int busId = busSystem.makeBus(0, 0, 0, 60, 65);
    commandInterpreter.getSimEngine().addNewEvent(commandInterpreter.getRank(), "move_bus", busId);
    RailSystem railSystem = commandInterpreter.getMartaRailSystem();
    int railStopOne = railSystem.makeStop(0, "Five Points");
    int railStopTwo = railSystem.makeStop(1, "Civic Center");
    int railRouteID = railSystem.makeRoute(0, 5, "Red");
    railSystem.appendStopToRoute(0, 0);
    railSystem.appendRouteToStop(0, 0);
    railSystem.appendStopToRoute(0, 1);
    railSystem.appendRouteToStop(0, 1);
  }
}
