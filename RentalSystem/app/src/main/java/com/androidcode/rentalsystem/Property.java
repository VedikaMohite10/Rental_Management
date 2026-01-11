package com.androidcode.rentalsystem;

import java.io.Serializable;

public class Property implements Serializable {

    private String title;
    private String location;
    private String price;
    private String type;
    private String description;
    private String pic;
    private String city;
    private String deposit;
    private String date;
    private String number;
    private int propertyId;

    public Property(int propertyId, String title, String location, String price, String type, String description, String city, String deposit, String date, String number, String pic) {
        this.propertyId = propertyId;
        this.title = title;
        this.location = location;
        this.price = price;
        this.type = type;
        this.description = description;
        this.city = city;
        this.deposit = deposit;
        this.date = date    ;
        this.number = number;
        this.pic=pic;
    }
    public int getPropertyId() {
        return propertyId;
    }


    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}