package edu.gatech;

import edu.gatech.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Bus Route.
 */
public class BusRoute  extends Route {

    public BusRoute() {
    }

    public BusRoute(int uniqueValue) {
        super(uniqueValue);
    }

    public BusRoute(int uniqueValue, int inputNumber, String inputName) {
        super(uniqueValue, inputNumber, inputName);
    }

    public void displayEvent() {
        System.out.println(" bus stop: " + Integer.toString(getID()));
    }

    public void takeTurn() {
        System.out.println("get new people - exchange with bus when it passes by");
    }

    public List<String> getRoadKeys() {
        ArrayList<String> roadIDs = new ArrayList<>(getLength());
        for (int i = 0; i < getLength() - 1; i++) {
            roadIDs.add(RoadInfo.roadKey(getStopID(i), getStopID(i + 1)));
        }
        roadIDs.add(RoadInfo.roadKey(getStopID(getLength() - 1), getStopID(0)));
        return roadIDs;
    }


}
