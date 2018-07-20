package edu.gatech.server;


import edu.gatech.Bus;
import edu.gatech.SimDriver;
import edu.gatech.SimQueue;
import edu.gatech.Vehicle;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class SimRunner implements  Runnable {
  private final SimDriver commandInterpreter;
  private final AtomicBoolean isRunning;
  private final LinkedBlockingQueue<String> runThreadCommands;

  private boolean continueRunningThread = true;
  private volatile RunReturn status = new RunReturn("idle");

  SimRunner(SimDriver commandInterpreter) {
    this.commandInterpreter = commandInterpreter;
    this.isRunning = new AtomicBoolean(false);
    this.runThreadCommands = new LinkedBlockingQueue<String>(1);
  }

  boolean stop() {
    try {
      runThreadCommands.put("quit");
      return true;
    } catch (InterruptedException e) {
      return false;
    }
  }

  boolean load() {
    return runThreadCommands.add("load");
  }

  boolean runFor(int steps) {
    return runThreadCommands.offer("run " + Integer.toString(steps));
  }

  boolean isIdle() {
    return runThreadCommands.isEmpty() && ! isRunning.get();
  }

  RunReturn getStatus() {
    return status;
  }

  @Override
  public void run() {
    try {
      while (continueRunningThread) {
        String nextCommand = runThreadCommands.poll(500, TimeUnit.MILLISECONDS);
        boolean shouldRun = (nextCommand != null) && isRunning.compareAndSet(false, true);
        if (shouldRun) {
          if (nextCommand.startsWith("run")) {
            int stepsRemaining = Integer.parseInt(nextCommand.substring(4));
            SimRunnerListener listener = new SimRunnerListener();
            SimQueue simEngine = commandInterpreter.getSimEngine();
            while (simEngine.getQueueSize() > 0 && stepsRemaining > 0) {
              simEngine.triggerNextEvent(
                  commandInterpreter.getMartaBusSystem(),
                  commandInterpreter.getMartaRailSystem(),
                  9,
                  listener);
              stepsRemaining--;
              listener.eventTriggered();
              status = listener.getActiveRunStatus();
            }
            listener.eventsFinished();
            status = listener.getActiveRunStatus();
          } else if (nextCommand.startsWith("load")) {
            commandInterpreter.uploadMARTAData();
          } else if (nextCommand.startsWith("quit")) {
            continueRunningThread = false;
          }
          isRunning.compareAndSet(true, false);
        }
      }
    } catch (InterruptedException e) {
      continueRunningThread = false;
    }
  }

  class SimRunnerListener implements SimQueue.QueueListener {
    private RunReturn activeRunStatus = new RunReturn("in_progress", new RunReturn.EventReturn[]{});
    private ArrayList<RunReturn.EventReturn> events = new ArrayList<>();
    private StringBuilder logString = new StringBuilder();
    private int vehicleId = -1;
    private String vehicleType = "";
    private int rank;

    void eventTriggered() {
      if (events.size() > 500) {
        events.remove(0);
      }
      events.add(new RunReturn.EventReturn(rank, vehicleId, vehicleType, logString.toString()));
      activeRunStatus = new RunReturn("in_progress", events.toArray(new RunReturn.EventReturn[]{}));
      logString = new StringBuilder();
    }

    void eventsFinished() {
      activeRunStatus = new RunReturn("idle", events.toArray(new RunReturn.EventReturn[]{}));
    }

    RunReturn getActiveRunStatus() {
      return activeRunStatus;
    }

    @Override
    public void currentRank(int rank) {
      this.rank = rank;
    }

    @Override
    public void reportVehicleMoved(Vehicle vehicle) {
      vehicleId = vehicle.getID();
      if (vehicle instanceof Bus)
        vehicleType = "bus";
      else
        vehicleType = "train";
    }

    @Override
    public void printInformation(String string) {
      logString.append(string);
      logString.append("\n");
    }
  }
}
