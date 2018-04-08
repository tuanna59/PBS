package team15.capstone2.pbs.models;

import java.io.Serializable;

public class ParkingLot implements Serializable {
    private int parkinglot_id;
    private String address;
    private int number_of_slots;
    private double cost;
    private int isActive;
    private int vendor_id;
    private double latitude;
    private double longitude;

    public ParkingLot() {
    }

    public ParkingLot(int parkinglotId, String address, int numberOfSlots, double cost, int isActive, int vendor_id, double latitude, double longitude) {
        this.parkinglot_id = parkinglotId;
        this.address = address;
        this.number_of_slots = numberOfSlots;
        this.cost = cost;
        this.isActive = isActive;
        this.vendor_id = vendor_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getParkinglotId() {
        return parkinglot_id;
    }

    public void setParkinglotId(int parkinglotId) {
        this.parkinglot_id = parkinglotId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfSlots() {
        return number_of_slots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.number_of_slots = numberOfSlots;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int isActive() {
        return isActive;
    }

    public void setActive(int active) {
        isActive = active;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
