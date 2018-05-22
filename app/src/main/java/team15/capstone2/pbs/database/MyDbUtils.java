package team15.capstone2.pbs.database;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import team15.capstone2.pbs.actitities.BookingActivity;
import team15.capstone2.pbs.actitities.MainActivity;
import team15.capstone2.pbs.models.BookingDetail;
import team15.capstone2.pbs.models.CarModel;
import team15.capstone2.pbs.models.ListBookingDetail;
import team15.capstone2.pbs.models.NotificationModel;
import team15.capstone2.pbs.models.ParkingLot;
import team15.capstone2.pbs.models.PaymentDetail;

public class MyDbUtils {
//    public final static String ip = "10.0.2.2";
    public final static String ip = "54.209.171.72";
    private static MyDbUtils instance = null;
    private ArrayList<ParkingLot> parkingLots;
    private ArrayList<BookingDetail> bookingDetails;
    private ArrayList<NotificationModel> notificationModels;
    private ArrayList<PaymentDetail> transactions;
    private ArrayList<CarModel> cars;
    private int clientID;
    private double balance;

    protected MyDbUtils() {
        // Exists only to defeat instantiation.
    }

    public static MyDbUtils getInstance() {
        if(instance == null) {
            instance = new MyDbUtils();
        }
        return instance;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<PaymentDetail> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<PaymentDetail> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<ParkingLot> getParkingLots() {
        return parkingLots;
    }

    public void setParkingLots(ArrayList<ParkingLot> parkingLots) {
        if (parkingLots == null) {
            parkingLots = new ArrayList<>();
        }
        this.parkingLots = parkingLots;
    }

    public ArrayList<BookingDetail> getBookingDetails() {
        if (bookingDetails == null) {
            bookingDetails = new ArrayList<>();
        }
        return bookingDetails;
    }

    public void setBookingDetails(ArrayList<BookingDetail> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public ArrayList<NotificationModel> getNotificationModels() {
        if (notificationModels == null) {
            notificationModels = new ArrayList<>();
        }
        return notificationModels;
    }

    public void setNotificationModels(ArrayList<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
    }

    public ArrayList<CarModel> getCars() {
        if (cars == null) {
            cars = new ArrayList<>();
        }
        return cars;
    }

    public void setCars(ArrayList<CarModel> cars) {
        this.cars = cars;
    }

    public ParkingLot findParkingLot(int id) {
        if (parkingLots == null)
            return null;
        for(ParkingLot d : parkingLots){
            if(d.getParkinglotId() == id) {
                return d;
            }
        }
        return null;
    }

    public String convertTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date parsed = format.parse(time); // => Date is in UTC now
            TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            SimpleDateFormat destFormat = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
            destFormat.setTimeZone(tz);
            String result = destFormat.format(parsed);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String convertRawTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date parsed = format.parse(time); // => Date is in UTC now
            TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            destFormat.setTimeZone(tz);
            String result = destFormat.format(parsed);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
