package edu.gatech;

import java.util.HashMap;

/**
 * Bus Stop.
 */
public class BusStop extends Stop {

    private HashMap<Integer, RoadInfo> roads;

    public HashMap<Integer, RoadInfo> getRoads() {
        return roads;
    }

    public void setRoads(HashMap<Integer, RoadInfo> roads) {
        this.roads = roads;
    }

    public BusStop() {
        super();
    }

    public BusStop(int uniqueValue) {
        super(uniqueValue);
    }

    public BusStop(int uniqueValue, String inputName,  double inputXCoord, double
            inputYCoord) {
        super(uniqueValue, inputName,  inputXCoord, inputYCoord);
    }



}
