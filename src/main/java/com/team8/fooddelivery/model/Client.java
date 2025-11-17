package com.team8.fooddelivery.model;

import java.time.Instant;
import java.util.*;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    private Long id;
    private String name;
    private String phone;
    private String passwordHash;
    private String email;

    private Address address;
    private ClientStatus status;
    private Instant createdAt = Instant.now();

    private boolean isActive = true;
    private Cart cart;

    private List<String> orderHistory;
}


