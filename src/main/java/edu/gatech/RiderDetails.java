package edu.gatech;

import java.util.List;

/**
 * RiderDetails
 */
public class RiderDetails {
    Integer id;
    String currentlocation;
    List<Rider> riders;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrentlocation() {
        return currentlocation;
    }

    public void setCurrentlocation(String currentlocation) {
        this.currentlocation = currentlocation;
    }

    public List<Rider> getRiders() {
        return riders;
    }

    public void setRiders(List<Rider> riders) {
        this.riders = riders;
    }

    @Override
    public String toString() {
        return "RiderDetails{" +
                "id=" + id +
                ", currentlocation='" + currentlocation + '\'' +
                ", riders=" + riders +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RiderDetails that = (RiderDetails) o;

        if (!id.equals(that.id)) return false;
        if (!currentlocation.equals(that.currentlocation)) return false;
        return riders != null ? riders.equals(that.riders) : that.riders == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + currentlocation.hashCode();
        return result;
    }

}
