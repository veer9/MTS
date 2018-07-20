package edu.gatech;

/**
 * RoadInfo
 */
public class RoadInfo {
    Integer roadId;
    Integer stop1;
    Integer stop2;
    Integer speedLimit;
    Integer routeId;
    Integer roadWork;
    String traffic;

    public RoadInfo(Integer roadId, Integer stop1, Integer stop2,
                    Integer speedLimit, Integer routeId, Integer roadWork, String traffic) {
        this.roadId = roadId;
        this.stop1 = stop1;
        this.stop2 = stop2;
        this.speedLimit = speedLimit;
        this.routeId = routeId;
        this.roadWork = roadWork;
        this.traffic = traffic;
    }

    public RoadInfo() {
        this.speedLimit = 10;
        this.traffic = "";
        this.roadWork = 0;
    }

    public Integer getRoadWork() {
        return roadWork;
    }

    public void setRoadWork(Integer roadWork) {
        this.roadWork = roadWork;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public Integer getRoadId() {
        return roadId;
    }

    public void setRoadId(Integer roadId) {
        this.roadId = roadId;
    }

    public Integer getStop1() {
        return stop1;
    }

    public void setStop1(Integer stop1) {
        this.stop1 = stop1;
    }

    public Integer getStop2() {
        return stop2;
    }

    public void setStop2(Integer stop2) {
        this.stop2 = stop2;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    //
    public Long getTravelTime(double travelDistance, Integer hourOfDay){
        // conversion is used to translate time calculation from hours to minutes
        if (this.getSpeedLimit() == null) return 0L;

        double travelTime = 1 + (travelDistance * 60 / getSpeedLimit());
        double tf=0.0;
        if (hourOfDay > 8 && hourOfDay < 19) //heavy traffice conditions
             tf = travelTime*0.5;
        else //normal
             tf =travelTime * 0.15;
        travelTime+=travelTime*tf+20*getRoadWork();//add 20 minutes for road work


        return Math.round(travelTime);
    }

    @Override
    public String toString() {
        return "RoadInfo{" +
                "roadId=" + roadId +
                ", stop1=" + stop1 +
                ", stop2=" + stop2 +
                ", speedLimit=" + speedLimit +
                ", routeId=" + routeId +
                ", roadWork=" + roadWork +
                ", traffic='" + traffic + '\'' +
                '}';
    }

    public static String roadKey(int start, int end) {
        return Integer.toString(start) + "-" + Integer.toString(end);
    }
}
