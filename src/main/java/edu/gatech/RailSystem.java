package edu.gatech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RailSystem extends TransitSystem{

    private HashMap<Integer, Train> trains;

    public RailSystem() {

        trains = new HashMap<Integer, Train>();
    }


   public List<RailStop> getRailStops(){
       List<RailStop> rstops = new ArrayList<>();

       for (Stop stop:this.getStops().values()){
           if (Stop.class.isInstance(RailStop.class)){
               rstops.add((RailStop)stop);
           }
       }

       return rstops;
   }

    public List<RailRoute> getRailRoutes(){
        List<RailRoute> rr = new ArrayList<>();

        for (Route rt:this.getRoutes().values()){
            if (Stop.class.isInstance(RailRoute.class)){
                rr.add((RailRoute)rt);
            }
        }
        return rr;
    }


    public Train getTrain(int trainID) {
        if (trains.containsKey(trainID)) { return trains.get(trainID); }
        return null;
    }

    public int makeStop(int uniqueID, String inputName) {
        // int uniqueID = stops.size();
        this.getStops().put(uniqueID, new RailStop(uniqueID, inputName));
        return uniqueID;
    }

    public int makeRoute(int uniqueID, int inputNumber, String inputName) {
        // int uniqueID = routes.size();
        this.getRoutes().put(uniqueID, new RailRoute(uniqueID, inputNumber, inputName));
        return uniqueID;
    }

    public int makeTrain(int uniqueID, int inputRoute, int inputLocation, int inputCapacity, int
            inputSpeed) {
        // int uniqueID = buses.size();
        trains.put(uniqueID, new Train(uniqueID, inputRoute, inputLocation, inputCapacity, inputSpeed));
        return uniqueID;
    }


    public HashMap<Integer, Train> getTrains() { return trains; }
    
    public void displayModel() {
    	ArrayList<MiniPair> trainNodes, stopNodes;
    	MiniPairComparator compareEngine = new MiniPairComparator();
    	
    	int[] colorScale = new int[] {9, 29, 69, 89, 101};
    	String[] colorName = new String[] {"#000077", "#0000FF", "#000000", "#770000", "#FF0000"};
    	Integer colorSelector, colorCount, colorTotal;
    	
    	try{
            // create new file access path
            String path="./mts_digraph.dot";
            File file = new File(path);

            // create the file if it doesn't exist
            if (!file.exists()) { file.createNewFile();}

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write("digraph G\n");
            bw.write("{\n");
    	
            trainNodes = new ArrayList<MiniPair>();
            for (Train b: trains.values()) { trainNodes.add(new MiniPair(b.getID(), b.getPassengers().size())); }
            Collections.sort(trainNodes, compareEngine);

            colorSelector = 0;
            colorCount = 0;
            colorTotal = trainNodes.size();
            for (MiniPair c: trainNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
            	bw.write("  train" + c.getID() + " [ label=\"train#" + c.getID() + " | " + c.getValue() + " riding\"," +
                        " " +
                        "color=\"" + colorName[colorSelector] + "\"];\n");
            }
            bw.newLine();
            
            stopNodes = new ArrayList<MiniPair>();
            for (Stop s: this.getRailStops()) { stopNodes.add(new MiniPair(s.getID(), s.getRidersAtStop().size
                    ())); }
            Collections.sort(stopNodes, compareEngine);

            colorSelector = 0;
            colorCount = 0;
            colorTotal = stopNodes.size();    	
            for (MiniPair t: stopNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
            	bw.write("  stop" + t.getID() + " [ label=\"stop#" + t.getID() + " | " + t.getValue() + " waiting\", color=\"" + colorName[colorSelector] + "\"];\n");
            }
            bw.newLine();
            
            for (Train m: trains.values()) {
            	Integer prevStop = this.getRailRoutes().get(m.getRouteID()).getStopID(m.getPastLocation());
            	Integer nextStop = this.getRailRoutes().get(m.getRouteID()).getStopID(m.getLocation());
            	bw.write("  stop" + Integer.toString(prevStop) + " -> train" + Integer.toString(m.getID()) + " [ " +
                        "label=\" dep\" ];\n");
            	bw.write("  train" + Integer.toString(m.getID()) + " -> stop" + Integer.toString(nextStop) + " [ " +
                        "label=\" arr\" ];\n");
            }
    	
            bw.write("}\n");
            bw.close();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}
