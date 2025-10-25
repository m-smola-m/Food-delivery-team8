package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Boolean read;

    public Notification() {
        this.createdAt = Instant.now();
        this.read = false;
    }

    public Notification(Long userId, String title, String message) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = Instant.now();
        this.read = false;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Boolean getRead() {
        return read;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
