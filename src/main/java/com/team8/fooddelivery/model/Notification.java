package com.team8.fooddelivery.model;

import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    private Long id;
    private Long clientId;
    private NotificationType type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
