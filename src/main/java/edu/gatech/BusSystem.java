package edu.gatech;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BusSystem extends TransitSystem{

    private HashMap<Integer, Bus> buses;
    private HashMap<String,RoadInfo> roads;

    public BusSystem() {
        super();
        buses = new HashMap<Integer, Bus>();
        roads = new HashMap<String, RoadInfo>();
    }

    public Bus getBus(int busID) {
        if (buses.containsKey(busID)) { return buses.get(busID); }
        return null;
    }

    public RoadInfo getRoads(String stopPair) {
        if (roads.containsKey(stopPair)) { return roads.get(stopPair); }
        return new RoadInfo();
    }

    public void addRoadInfo(RoadInfo info){
        roads.put(info.getStop1()+"-"+info.getStop2(), info);
    }

    public int makeRoadInfo(int uniqueID, int route, int stop1, int stop2, int roadWork,int speed, String traffic) {
        RoadInfo rf = new RoadInfo(uniqueID, stop1, stop2, speed, route, roadWork, traffic);
        addRoadInfo(rf);
        return uniqueID;
    }

    public RoadInfo getRoadInfoBetweenStops(int stop1, int stop2){
        return roads.get(stop1+"-"+stop2);
    }

    public List<RoadInfo> getAllRoadInfo(){
        List<RoadInfo> rfList = new ArrayList<RoadInfo>();
        for (RoadInfo rf: roads.values()){
            rfList.add(rf);
        }
       return rfList;
    }

    public int makeStop(int uniqueID, String inputName,  double inputXCoord, double
            inputYCoord) {
        // int uniqueID = stops.size();
        this.getStops().put(uniqueID, new BusStop(uniqueID, inputName,  inputXCoord, inputYCoord));
        return uniqueID;
    }

    public int makeRoute(int uniqueID, int inputNumber, String inputName) {
        // int uniqueID = routes.size();
        this.getRoutes().put(uniqueID, new BusRoute(uniqueID, inputNumber, inputName));
        return uniqueID;
    }

    public int makeBus(int uniqueID, int inputRoute, int inputLocation,  int inputCapacity, int inputSpeed) {
        // int uniqueID = buses.size();
        buses.put(uniqueID, new Bus(uniqueID, inputRoute, inputLocation,  inputCapacity, inputSpeed));
        return uniqueID;
    }

    public List<BusStop> getBusStops(){
        List<BusStop> bstops = new ArrayList<>();

        for (Stop stop:this.getStops().values()){
            if (Stop.class.isInstance(BusStop.class)){
                bstops.add((BusStop)stop);
            }
        }

        return bstops;
    }

    public List<BusRoute> getBusRoutes(){
        List<BusRoute> bstops = new ArrayList<>();

        for (Route rt:this.getRoutes().values()){
            if (Stop.class.isInstance(BusRoute.class)){
                bstops.add((BusRoute)rt);
            }
        }

        return bstops;
    }


    public HashMap<Integer, Bus> getBuses() { return buses; }
    
    public void displayModel() {
    	ArrayList<MiniPair> busNodes, stopNodes;
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
    	
            busNodes = new ArrayList<MiniPair>();
            for (Bus b: buses.values()) { busNodes.add(new MiniPair(b.getID(), b.getPassengers().size())); }
            Collections.sort(busNodes, compareEngine);

            colorSelector = 0;
            colorCount = 0;
            colorTotal = busNodes.size();
            for (MiniPair c: busNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
            	bw.write("  bus" + c.getID() + " [ label=\"bus#" + c.getID() + " | " + c.getValue() + " riding\", color=\"" + colorName[colorSelector] + "\"];\n");
            }
            bw.newLine();
            
            stopNodes = new ArrayList<MiniPair>();
            for (Stop s: this.getBusStops()) {
                int waiting_pass=0;
                    waiting_pass+=s.getRidersAtStop().size();
                stopNodes.add(new MiniPair(s.getID(), waiting_pass));
            }
            Collections.sort(stopNodes, compareEngine);

            colorSelector = 0;
            colorCount = 0;
            colorTotal = stopNodes.size();    	
            for (MiniPair t: stopNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
            	bw.write("  stop" + t.getID() + " [ label=\"stop#" + t.getID() + " | " + t.getValue() + " waiting\", color=\"" + colorName[colorSelector] + "\"];\n");
            }
            bw.newLine();
            
            for (Bus m: buses.values()) {
            	Integer prevStop = this.getBusRoutes().get(m.getRouteID()).getStopID(m.getPastLocation());
            	Integer nextStop = this.getBusRoutes().get(m.getRouteID()).getStopID(m.getLocation());
            	bw.write("  stop" + Integer.toString(prevStop) + " -> bus" + Integer.toString(m.getID()) + " [ label=\" dep\" ];\n");
            	bw.write("  bus" + Integer.toString(m.getID()) + " -> stop" + Integer.toString(nextStop) + " [ label=\" arr\" ];\n");
            }
    	
            bw.write("}\n");
            bw.close();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}
