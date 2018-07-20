package edu.gatech.server;

public class RunReturn {
  private String status;
  private EventReturn[] events;

  public RunReturn(String status) {
    this(status, new EventReturn[]{});
  }

  public RunReturn(String status, EventReturn[] events) {
    this.status = status;
    this.events = events;
  };

  static class EventReturn {
    private int vehicleId;
    private String log;
    private String vehicleType;
    private int rank;

    public EventReturn(int rank, int vehicleId, String vehicleType, String log) {
      this.vehicleId = vehicleId;
      this.vehicleType = vehicleType;
      this.log = log;
      this.rank = rank;
    }
  }
}
