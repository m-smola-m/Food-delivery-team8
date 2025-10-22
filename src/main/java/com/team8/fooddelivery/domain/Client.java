package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "clients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    public Client() {
        this.createdAt = Instant.now();
        this.isActive = true;
    }

    public Client(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = Instant.now();
        this.isActive = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Instant getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }
    public Cart getCart() { return cart; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setCart(Cart cart) { this.cart = cart; }
}
