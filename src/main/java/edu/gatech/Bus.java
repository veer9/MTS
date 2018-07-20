package edu.gatech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bus extends Vehicle{


    public Bus() {
        super();
    }

    public Bus(int uniqueValue) {
        super(uniqueValue);
    }

    public Bus(int uniqueValue, int inputRoute, int inputLocation,  int inputCapacity, int
            inputSpeed) {
       super( uniqueValue,  inputRoute,  inputLocation,   inputCapacity,
        inputSpeed);
   }


}
