package com.sgtech.qr_scanner.database;

public class DataModel {
    String data_type;
    String data_name;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DataModel(String data_type, String data_name, String date) {
        this.data_type = data_type;
        this.data_name = data_name;
        this.date = date;
    }

    public DataModel(String data_type, String data_name) {
        this.data_type = data_type;
        this.data_name = data_name;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getData_name() {
        return data_name;
    }

    public void setData_name(String data_name) {
        this.data_name = data_name;
    }
}
