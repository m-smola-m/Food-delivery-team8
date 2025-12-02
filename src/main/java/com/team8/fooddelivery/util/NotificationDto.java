package com.team8.fooddelivery.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long clientId;
    private String type;
    private String message;
    private String timestamp;
    private boolean isRead;
}


