package com.team8.fooddelivery.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String passwordHash;
    private Instant createdAt = Instant.now();
    private boolean isActive = true;
    private Cart cart;
}


