package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;

public class Address {
  private String country;
  private String city;
  private String street;
  private Integer building;
  private Integer apartment;
  private Integer entrance;
  private Integer floor;
  private double latitude;
  private double longitude;
  private String addressNote;

  public Address(String country, String city, String street, Integer building,
                 Integer apartment, Integer entrance, Integer floor,
                 double latitude, double longitude, String addressNote) {
    this.country = country;
    this.city = city;
    this.street = street;
    this.building = building;
    this.apartment = apartment;
    this.entrance = entrance;
    this.floor = floor;
    this.latitude = latitude;
    this.longitude = longitude;
    this.addressNote = addressNote;
  }

  public String getCountry() { return country; }
  public String getCity() { return city; }
  public String getStreet() { return street; }
  public Integer getBuilding() { return building; }
  public Integer getApartment() { return apartment; }
  public Integer getEntrance() { return entrance; }
  public Integer getFloor() { return floor; }
  public double getLatitude() { return latitude; }
  public double getLongitude() { return longitude; }
  public String getAddressNote() { return addressNote; }

  public void setCountry(String country) { this.country = country; }
  public void setCity(String city) { this.city = city; }
  public void setStreet(String street) { this.street = street; }
  public void setBuilding(Integer building) { this.building = building; }
  public void setApartment(Integer apartment) { this.apartment = apartment; }
  public void setEntrance(Integer entrance) { this.entrance = entrance; }
  public void setFloor(Integer floor) { this.floor = floor; }
  public void setLatitude(double latitude) { this.latitude = latitude; }
  public void setLongitude(double longitude) { this.longitude = longitude; }
  public void setAddressNote(String addressNote) { this.addressNote = addressNote; }

}
