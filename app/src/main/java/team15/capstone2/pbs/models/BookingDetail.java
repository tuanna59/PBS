package team15.capstone2.pbs.models;

import java.io.Serializable;

import team15.capstone2.pbs.database.MyDbUtils;

public class BookingDetail implements Serializable {
    private int booking_detail_id;
    private int client_id;
    private int parkinglot_id;
    private int status;
    private String start_time;
    private String end_time;
    private String booking_time;

    public BookingDetail() {
    }

    public BookingDetail(int booking_detail_id, int client_id, int parkinglot_id, int status, String start_time, String end_time, String booking_time) {
        this.booking_detail_id = booking_detail_id;
        this.client_id = client_id;
        this.parkinglot_id = parkinglot_id;
        this.status = status;
        this.start_time = start_time;
        this.end_time = end_time;
        this.booking_time = booking_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getBooking_detail_id() {
        return booking_detail_id;
    }

    public void setBooking_detail_id(int booking_detail_id) {
        this.booking_detail_id = booking_detail_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getParkinglot_id() {
        return parkinglot_id;
    }

    public void setParkinglot_id(int parkinglot_id) {
        this.parkinglot_id = parkinglot_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }
}
