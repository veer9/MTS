package edu.gatech;

/**
 * .
 */
public class TransitStop {

    Integer railRoute;
    Integer railStop;
    Integer busRoute;
    Integer busStop;

    public Integer getRailRoute() {
        return railRoute;
    }

    public void setRailRoute(Integer railRoute) {
        this.railRoute = railRoute;
    }

    public Integer getRailStop() {
        return railStop;
    }

    public void setRailStop(Integer railStop) {
        this.railStop = railStop;
    }


    public TransitStop(Integer railRoute, Integer railStop, Integer busRoute, Integer busStop) {
        this.railRoute = railRoute;
        this.railStop = railStop;
        this.busRoute = busRoute;
        this.busStop = busStop;
    }

    public Integer getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(Integer busRoute) {
        this.busRoute = busRoute;
    }

    public Integer getBusStop() {
        return busStop;
    }

    public void setBusStop(Integer busStop) {
        this.busStop = busStop;
    }

    @Override
    public String toString() {
        return "TransitStop{" +
                "railRoute=" + railRoute +
                ", railStop=" + railStop +
                ", busRoute=" + busRoute +
                ", busStop=" + busStop +
                '}';
    }
}
