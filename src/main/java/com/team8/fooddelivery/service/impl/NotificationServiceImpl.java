package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.model.notification.NotificationTemplate;
import com.team8.fooddelivery.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NotificationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final NotificationServiceImpl INSTANCE = new NotificationServiceImpl();

    private final NotificationRepository notificationRepository = new NotificationRepository();

    private NotificationServiceImpl() {
    }

    public static NotificationServiceImpl getInstance() {
        return INSTANCE;
    }

    public void notify(Long clientId, NotificationTemplate template, Object... args) {
        Notification notification = Notification.builder()
                .clientId(clientId)
                .type(template.getType())
                .message(template.format(args))
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
        try {
            notificationRepository.save(notification);
        } catch (SQLException e) {
            logger.error("Не удалось сохранить уведомление", e);
        }
        logger.info("[{}] Notification for client {}: {}", template.getType(), clientId, notification.getMessage());
    }

    public void notifyAccount(Long clientId, String messageArg) {
        notify(clientId, NotificationTemplate.PROFILE_UPDATED, messageArg);
    }

    public void notifyWelcome(Long clientId, String clientName) {
        notify(clientId, NotificationTemplate.WELCOME_ACCOUNT, clientName);
    }

    public void notifyOrderPlaced(Long clientId, long orderId, long price) {
        notify(clientId, NotificationTemplate.ORDER_PLACED, orderId, price);
    }

    public void notifyOrderPaid(Long clientId, long orderId) {
        notify(clientId, NotificationTemplate.ORDER_PAID, orderId);
    }

    public void notifyDelivery(Long clientId, long orderId) {
        notify(clientId, NotificationTemplate.ORDER_DELIVERED, orderId);
    }

    public List<Notification> getNotifications(Long clientId) {
        try {
            return notificationRepository.findByClientId(clientId);
        } catch (SQLException e) {
            logger.error("Не удалось загрузить уведомления", e);
            return Collections.emptyList();
        }
    }

    public void clear(Long clientId) {
        try {
            notificationRepository.markAllAsRead(clientId);
        } catch (SQLException e) {
            logger.error("Не удалось обновить уведомления", e);
        }
        logger.info("Notifications cleared for client {}", clientId);
    }
}
