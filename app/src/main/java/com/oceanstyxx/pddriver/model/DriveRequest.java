package com.oceanstyxx.pddriver.model;

import java.util.ArrayList;

import static com.oceanstyxx.pddriver.R.id.driverCode;

/**
 * Created by mohsin on 19/10/16.
 */

public class DriveRequest {

    private String id;

    private String drive_code;

    private String status;

    private String booking_date_time;

    private String drive_end_time;

    private String total_travel_time;

    private String total_drive_rate;


    private String drive_start_time;


    private Pub pub;

    private ArrayList<Billing> billing;

    private OtherVenue othervenue;

    private String pickup_src;

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OtherVenue getOthervenue() {
        return othervenue;
    }

    public void setOthervenue(OtherVenue othervenue) {
        this.othervenue = othervenue;
    }

    public String getPickup_src() {
        return pickup_src;
    }

    public void setPickup_src(String pickup_src) {
        this.pickup_src = pickup_src;
    }


    public ArrayList getBilling() {
        return billing;
    }

    public void setBilling(ArrayList billing) {
        this.billing = billing;
    }

    public Pub getPub() {
        return pub;
    }

    public void setPub(Pub pub) {
        this.pub = pub;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getBooking_date_time() {
        return booking_date_time;
    }

    public void setBooking_date_time(String booking_date_time) {
        this.booking_date_time = booking_date_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDrive_code() {
        return drive_code;
    }

    public void setDrive_code(String drive_code) {
        this.drive_code = drive_code;
    }

    public String getDrive_start_time() {
        return drive_start_time;
    }

    public void setDrive_start_time(String drive_start_time) {
        this.drive_start_time = drive_start_time;
    }

    public String getDrive_end_time() {
        return drive_end_time;
    }

    public void setDrive_end_time(String drive_end_time) {
        this.drive_end_time = drive_end_time;
    }

    public String getTotal_travel_time() {
        return total_travel_time;
    }

    public void setTotal_travel_time(String total_travel_time) {
        this.total_travel_time = total_travel_time;
    }

    public String getTotal_drive_rate() {
        return total_drive_rate;
    }

    public void setTotal_drive_rate(String total_drive_rate) {
        this.total_drive_rate = total_drive_rate;
    }



}
