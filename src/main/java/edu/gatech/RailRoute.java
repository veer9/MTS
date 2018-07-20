package edu.gatech;

import edu.gatech.Route;

/**
 * Bus Route.
 */
public class RailRoute extends Route {


    String direction;

    public RailRoute() {
    }

    public RailRoute(int uniqueValue) {
        super(uniqueValue);
    }

    public RailRoute(int uniqueValue, int inputNumber, String inputName) {
        super(uniqueValue, inputNumber, inputName);
    }

    public void displayEvent() {
        System.out.println(" train stop: " + Integer.toString(getID()));
    }

    public void takeTurn() {
        System.out.println("get new people - exchange with train when it passes by");
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RailRoute railRoute = (RailRoute) o;

        return direction != null ? direction.equals(railRoute.direction) : railRoute.direction == null;

    }

    @Override
    public int hashCode() {
        return direction != null ? direction.hashCode() : 0;
    }
}
