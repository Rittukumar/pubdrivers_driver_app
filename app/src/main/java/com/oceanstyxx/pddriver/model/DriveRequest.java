package com.oceanstyxx.pddriver.model;

import static com.oceanstyxx.pddriver.R.id.driverCode;

/**
 * Created by mohsin on 19/10/16.
 */

public class DriveRequest {

    private String id;

    private String drive_code;

    private String status;

    private String booking_date_time;

    private String driveStartDate;

    private String driveEndDate;

    private String totalTravelTime;

    private String totalDriveRate;

    private Pub pub;

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

    public String getTotalDriveRate() {
        return totalDriveRate;
    }

    public void setTotalDriveRate(String totalDriveRate) {
        this.totalDriveRate = totalDriveRate;
    }

    public String getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(String totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    public String getDriveEndDate() {
        return driveEndDate;
    }

    public void setDriveEndDate(String driveEndDate) {
        this.driveEndDate = driveEndDate;
    }

    public String getDriveStartDate() {
        return driveStartDate;
    }

    public void setDriveStartDate(String driveStartDate) {
        this.driveStartDate = driveStartDate;
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

}
