package team15.capstone2.pbs.database;

import java.util.ArrayList;

import team15.capstone2.pbs.models.ParkingLot;

public class MyDbUtils {
    public final static String ip = "10.0.2.2";   // "10.0.2.2"  "54.209.171.72"
    private static MyDbUtils instance = null;
    private ArrayList<ParkingLot> parkingLots;

    protected MyDbUtils() {
        // Exists only to defeat instantiation.
    }

    public static MyDbUtils getInstance() {
        if(instance == null) {
            instance = new MyDbUtils();
        }
        return instance;
    }

    public ArrayList<ParkingLot> getParkingLots() {
        return parkingLots;
    }

    public void setParkingLots(ArrayList<ParkingLot> parkingLots) {
        this.parkingLots = parkingLots;
    }
}
