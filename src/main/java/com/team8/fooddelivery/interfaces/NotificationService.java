package com.team8.fooddelivery.interfaces;

import com.team8.fooddelivery.model.Notification;
import java.util.List;

public interface NotificationService {
    void send(Long userId, String title, String message);
    List<Notification> listForUser(Long userId);
    void markAllRead(Long userId);
}


