package com.example.librecord;

public class ReservationRecord {
    private int id;
    private String name;
    private String details;
    private String status;
    private String date;

    public ReservationRecord(int id, String name, String details,String status,String date) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
    public String getStatus() {
        return status;
    }
    public String getDate() {
        return date;
    }
}
