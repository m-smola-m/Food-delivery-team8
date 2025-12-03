package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final NotificationService INSTANCE = new NotificationService();
    private final NotificationRepository notificationRepository = new NotificationRepository();

    private NotificationService() {}

    public static NotificationService getInstance() {
        return INSTANCE;
    }

    public Notification sendNotification(Long clientId, String type, String message) {
        Notification notification = Notification.builder()
                .clientId(clientId)
                .type(type)
                .message(message)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
        try {
            notificationRepository.save(notification);
        } catch (SQLException e) {
            logger.error("Не удалось сохранить уведомление", e);
        }
        return notification;
    }

    public void notifyAccount(Long clientId, String message) {
        sendNotification(clientId, "ACCOUNT", message);
    }

    public void notifyOrder(Long clientId, String message) {
        sendNotification(clientId, "ORDER", message);
    }

    public void notifyDelivery(Long clientId, String message) {
        sendNotification(clientId, "DELIVERY", message);
    }

    public List<Notification> getNotifications(Long clientId) {
        try {
            return notificationRepository.findByClientId(clientId);
        } catch (SQLException e) {
            logger.error("Не удалось загрузить уведомления", e);
            return Collections.emptyList();
        }
    }

    public void markAllAsRead(Long clientId) {
        try {
            notificationRepository.markAllAsRead(clientId);
        } catch (SQLException e) {
            logger.error("Не удалось обновить уведомления", e);
        }
    }
}
