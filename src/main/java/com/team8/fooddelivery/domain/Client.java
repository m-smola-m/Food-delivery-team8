package com.team8.fooddelivery.domain;

import java.time.Instant;

// @Data
// @AllArgsConstructor
public class Client {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Instant createdAt= Instant.now();
    private Boolean isActive = true;
    private Cart cart;

    // public Client() {
    //     this.createdAt = Instant.now();
    //     this.isActive = true;
    // }

    // public Client(String name, String email, String phone, Address address) {
    //     this.name = name;
    //     this.email = email;
    //     this.phone = phone;
    //     this.address = address;
    //     this.createdAt = Instant.now();
    //     this.isActive = true;
    // }

    // public Long getId() { return id; }
    // public String getName() { return name; }
    // public String getEmail() { return email; }
    // public String getPhone() { return phone; }
    // public String getAddress() { return address; }
    // public Instant getCreatedAt() { return createdAt; }
    // public Boolean getIsActive() { return isActive; }
    // public Cart getCart() { return cart; }

    // public void setId(Long id) { this.id = id; }
    // public void setName(String name) { this.name = name; }
    // public void setEmail(String email) { this.email = email; }
    // public void setPhone(String phone) { this.phone = phone; }
    // public void setAddress(Address address) { this.address = address; }
    // public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    // public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    // public void setCart(Cart cart) { this.cart = cart; }
}
