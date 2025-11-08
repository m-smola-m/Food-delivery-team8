package com.team8.fooddelivery.model;

import lombok.*;

@Data
@AllArgsConstructor
public class WorkingHours {
  private String monday;
  private String tuesday;
  private String wednesday;
  private String thursday;
  private String friday;
  private String saturday;
  private String sunday;
}
