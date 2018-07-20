package edu.gatech;

import java.util.ArrayList;
import java.util.List;

public class Train extends Vehicle{

    //capacity - fixed value

    public Train() {
        super();
    }

    public Train(int uniqueValue) {
        super(uniqueValue);
    }

    public Train(int uniqueValue, int inputRoute, int inputLocation, int inputCapacity, int inputSpeed) {
        super( uniqueValue,  inputRoute,  inputLocation,  inputCapacity,  inputSpeed);
   }

   public Train(int uniqueValue, int inputRoute){super( uniqueValue,  inputRoute,0,100,55);}

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
