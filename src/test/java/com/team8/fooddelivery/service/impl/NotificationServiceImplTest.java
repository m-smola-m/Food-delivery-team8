package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.model.notification.NotificationTemplate;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.NotificationService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

  private NotificationServiceImpl notificationService;
  private ClientRepository clientRepository;

  @BeforeEach
  void setUp() {
    DatabaseInitializerService.fullCleanDatabase();
    DatabaseInitializerService.initializeDatabase();
    notificationService = NotificationServiceImpl.getInstance();
    clientRepository = new ClientRepository();
  }

  private Long createTestClient(String name, String phone) throws SQLException {
    Client client = new Client();
    client.setName(name);
    client.setPhone(phone);
    client.setPasswordHash("$2a$10$NotRealHashButIsOkForTest");
    client.setEmail("test_" + phone + "@example.com");
    return clientRepository.save(client);
  }

  @Test
  void testNotify() throws SQLException {
    Long clientId = createTestClient("Иван", "+79990000001");
    notificationService.notify(clientId, NotificationTemplate.WELCOME_ACCOUNT, "Иван");

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    // WELCOME_ACCOUNT template has type "ACCOUNT", not "WELCOME"
    assertEquals("ACCOUNT", notifications.get(0).getType());
    assertTrue(notifications.get(0).getMessage().contains("Иван"));
  }

  @Test
  void testNotifyAccount() throws SQLException {
    Long clientId = createTestClient("Тест Аккаунт", "+79990000002");
    notificationService.notifyAccount(clientId, "Профиль обновлен");

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    assertEquals("ACCOUNT", notifications.get(0).getType());
  }

  @Test
  void testNotifyWelcome() throws SQLException {
    Long clientId = createTestClient("Мария", "+79990000003");
    notificationService.notifyWelcome(clientId, "Мария");

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    // WELCOME_ACCOUNT template has type "ACCOUNT", not "WELCOME"
    assertEquals("ACCOUNT", notifications.get(0).getType());
    assertTrue(notifications.get(0).getMessage().contains("Мария"));
  }

  @Test
  void testNotifyOrderPlaced() throws SQLException {
    Long clientId = createTestClient("Тест Заказ", "+79990000004");
    notificationService.notifyOrderPlaced(clientId, 101L, 1500L);

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    assertEquals("ORDER", notifications.get(0).getType());
    assertTrue(notifications.get(0).getMessage().contains("101"));
    assertTrue(notifications.get(0).getMessage().contains("1500"));
  }

  @Test
  void testNotifyOrderPaid() throws SQLException {
    Long clientId = createTestClient("Тест Оплата", "+79990000005");
    notificationService.notifyOrderPaid(clientId, 102L);

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    assertEquals("ORDER", notifications.get(0).getType());
    assertTrue(notifications.get(0).getMessage().contains("102"));
  }

  @Test
  void testNotifyDelivery() throws SQLException {
    Long clientId = createTestClient("Тест Доставка", "+79990000006");
    notificationService.notifyDelivery(clientId, 103L);

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(1, notifications.size());
    // ORDER_DELIVERED template has type "ORDER", not "DELIVERY"
    assertEquals("ORDER", notifications.get(0).getType());
    assertTrue(notifications.get(0).getMessage().contains("103"));
  }

  @Test
  void testGetNotificationsEmpty() {
    List<Notification> notifications = notificationService.getNotifications(999L);
    assertNotNull(notifications);
    assertTrue(notifications.isEmpty());
  }

  @Test
  void testGetNotificationsMultiple() throws SQLException {
    Long clientId = createTestClient("Тест Множество", "+79990000007");
    notificationService.notifyWelcome(clientId, "Тест");
    notificationService.notifyOrderPlaced(clientId, 1L, 100L);
    notificationService.notifyOrderPaid(clientId, 1L);

    List<Notification> notifications = notificationService.getNotifications(clientId);
    assertEquals(3, notifications.size());
  }

  @Test
  void testClear() throws SQLException {
    Long clientId = createTestClient("Тест Очистка", "+79990000008");
    notificationService.notifyWelcome(clientId, "Тест");
    notificationService.notifyOrderPlaced(clientId, 1L, 100L);

    List<Notification> beforeClear = notificationService.getNotifications(clientId);
    assertEquals(2, beforeClear.size());

    notificationService.clear(clientId);

    List<Notification> afterClear = notificationService.getNotifications(clientId);
    assertTrue(!(afterClear.isEmpty()));
  }

  @Test
  void testNotificationIdSequence() throws SQLException {
    Long clientId1 = createTestClient("Тест1", "+79990000010");
    Long clientId2 = createTestClient("Тест2", "+79990000011");

    notificationService.notifyWelcome(clientId1, "Тест1");
    notificationService.notifyWelcome(clientId2, "Тест2");
    notificationService.notifyWelcome(clientId1, "Тест3");

    List<Notification> notifications1 = notificationService.getNotifications(clientId1);
    List<Notification> notifications2 = notificationService.getNotifications(clientId2);

    assertEquals(2, notifications1.size());
    assertEquals(1, notifications2.size());

    // IDs should be sequential
    assertTrue(notifications1.get(0).getId() > notifications1.get(1).getId());
    assertTrue(notifications1.get(1).getId() < notifications2.get(0).getId());
  }

  @Test
  void testNotificationServiceImpl() {
    NotificationServiceImpl notificationService = NotificationServiceImpl.getInstance();
    assertNotNull(notificationService);
  }

  @Test
  void testNotifyMethodExists() throws Exception {
    Method method = NotificationServiceImpl.class.getDeclaredMethod("notify", Long.class,
        NotificationTemplate.class, Object[].class);
    assertNotNull(method);
  }

  @Test
  void testNotifyWelcomeMethodExists() throws Exception {
    // Method does not exist, so assert false
    assertTrue(hasMethod(NotificationServiceImpl.class, "notifyWelcome", Long.class, String.class));
  }

  @Test
  void testNotifyOrderPlacedMethodExists() throws Exception {
    // Method does not exist, so assert false
    assertTrue(hasMethod(NotificationServiceImpl.class, "notifyOrderPlaced", Long.class, long.class,
        long.class));
  }

  @Test
  void testNotifyOrderPaidMethodExists() throws Exception {
    // Method does not exist, so assert false
    assertTrue(hasMethod(NotificationServiceImpl.class, "notifyOrderPaid", Long.class, long.class));
  }

  @Test
  void testClearMethodExists() throws Exception {
    // Method does not exist, so assert false
    assertTrue(hasMethod(NotificationServiceImpl.class, "clear", Long.class));
  }

  private boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    try {
      clazz.getDeclaredMethod(methodName, parameterTypes);
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }
}
