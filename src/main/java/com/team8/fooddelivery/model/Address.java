package com.team8.fooddelivery.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class Address {
  private Long id;
  private String country;
  private String city;
  private String street;
  private String building;
  private String apartment;
  private String entrance;
  private Integer floor;
  private Double latitude;
  private Double longitude;
  private String addressNote;
  private String district;

  //public Address() {}
}