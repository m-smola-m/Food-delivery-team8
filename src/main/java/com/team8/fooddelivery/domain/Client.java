package com.team8.fooddelivery.domain;

import java.time.Instant;

public class Client {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Instant createdAt= Instant.now();
    private Boolean isActive = true;
    private Cart cart;

}
