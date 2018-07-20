package edu.gatech;

public class SimEvent {
    private Integer timeRank;
    private String eventType;
    private Integer eventID;

    @Override
    public String toString() {
        return "SimEvent{" +
                "timeRank=" + timeRank +
                ", eventType='" + eventType + '\'' +
                ", eventID=" + eventID +
                '}';
    }

    public SimEvent() { this.timeRank = 0; }

    public SimEvent(int inputRank, String inputType, int inputID) {
        this.timeRank= inputRank;
        this.eventType = inputType;
        this.eventID = inputID;
    }

    public void setRank(int inputRank) { this.timeRank = inputRank; }

    public void setType(String inputType) { this.eventType = inputType; }

    public void setID(int inputID) { this.eventID = inputID; }

    public Integer getRank() { return this.timeRank; }

    public String getType() { return this.eventType; }

    public Integer getID() { return this.eventID; }

    public void displayEvent() {
        // System.out.println();
        System.out.println("# event rank: " + Integer.toString(timeRank) + " type: " + eventType + " ID: " + Integer.toString(eventID));
    }

    //Override the equals method to compare the object
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            SimEvent me = (SimEvent) object;
            if (this.timeRank.intValue() == me.getRank().intValue() )
                    if( this.eventType.equalsIgnoreCase(me.getType()) )
                        if( this.eventID.intValue()== me.getID().intValue() ) {
                                 result = true;
                         }
                         else{
                            System.out.print("eventid not same");
                        }
                    else{
                        System.out.print("event type not same");
                    }
            else{
                System.out.print("time rank not same");
            }
        }

        return result;
    }

}
