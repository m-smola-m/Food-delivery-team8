package com.team8.fooddelivery.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long productId;
    private Long clientId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
