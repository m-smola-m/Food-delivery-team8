package com.team8.fooddelivery.service.imp;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.service.ClientService;
import com.team8.fooddelivery.util.PasswordUtils;
import com.team8.fooddelivery.util.ValidationUtils;
import com.team8.fooddelivery.util.SimpleLogger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ClientServiceImp implements ClientService {

    private static final SimpleLogger logger = new SimpleLogger(ClientServiceImp.class);

    private static final AtomicLong ID_SEQ = new AtomicLong(1);
    private static final Map<Long, Client> ID_TO_CLIENT = new HashMap<>();
    private static final Map<Long, List<String>> ID_TO_ORDER_HISTORY = new HashMap<>();

    // =====================
    // Регистрация
    // =====================
    public Client register(String name, String email, String phone, Address address, String password) {
        logger.info("Попытка регистрации пользователя: " + email);

        if (email == null || email.isBlank()) {
            logger.warn("Регистрация не удалась: пустой email");
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (password == null || password.isBlank()) {
            logger.warn("Регистрация не удалась: пустой пароль для " + email);
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            logger.warn("Регистрация не удалась: неверный формат email: " + email);
            throw new IllegalArgumentException("Неверный формат email");
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            logger.warn("Регистрация не удалась: неверный формат телефона: " + phone);
            throw new IllegalArgumentException("Неверный формат телефона");
        }
        if (!ValidationUtils.isValidAddress(address)) {
            logger.warn("Регистрация не удалась: неверный адрес для пользователя " + email);
            throw new IllegalArgumentException("Неверный формат адреса");
        }
        if (!ValidationUtils.isValidPassword(password)) {
            logger.warn("Регистрация не удалась: слабый пароль для " + email);
            throw new IllegalArgumentException("Слабый пароль");
        }

        if (isEmailExists(email)) {
            logger.warn("Регистрация не удалась: email уже существует: " + email);
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (isPhoneExists(phone)) {
            logger.warn("Регистрация не удалась: телефон уже существует: " + phone);
            throw new IllegalArgumentException("Пользователь с таким телефоном уже существует");
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        Long id = ID_SEQ.getAndIncrement();

        Client client = new Client(id, name, email, phone, address, hashedPassword, Instant.now(), true, new Cart());

        ID_TO_CLIENT.put(id, client);
        ID_TO_ORDER_HISTORY.put(id, new ArrayList<>());

        logger.info("Пользователь успешно зарегистрирован: " + email + ", id=" + id);
        return client;
    }

    // =====================
    // Аутентификация
    // =====================
    public boolean authenticate(String email, String password) {
        logger.info("Попытка аутентификации пользователя: " + email);

        for (Client client : ID_TO_CLIENT.values()) {
            if (client.getEmail().equals(email) && client.isActive()) {
                boolean result = PasswordUtils.checkPassword(password, client.getPasswordHash());
                if (result) {
                    logger.info("Пользователь успешно аутентифицирован: " + email);
                } else {
                    logger.warn("Неудачная аутентификация: неверный пароль для " + email);
                }
                return result;
            }
        }
        logger.warn("Неудачная аутентификация: пользователь не найден или деактивирован: " + email);
        return false;
    }

    // =====================
    // Обновление клиента
    // =====================
    @Override
    public Client update(Long clientId, String name, String email, String phone, Address address) {
        logger.info("Попытка обновления данных клиента id=" + clientId);

        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) {
            logger.warn("Обновление не удалось: клиент не найден id=" + clientId);
            throw new IllegalArgumentException("Клиент не найден");
        }

        if (Objects.equals(existing.getName(), name)
                && Objects.equals(existing.getEmail(), email)
                && Objects.equals(existing.getPhone(), phone)
                && Objects.equals(existing.getAddress(), address)) {
            logger.warn("Обновление не удалось: изменений не обнаружено для id=" + clientId);
            throw new IllegalArgumentException("Изменений не обнаружено");
        }

        if (email != null && !email.equals(existing.getEmail())) {
            if (!ValidationUtils.isValidEmail(email)) {
                logger.warn("Некорректный email при обновлении: " + email);
                throw new IllegalArgumentException("Неверный формат email");
            }
            if (isEmailExists(email)) {
                logger.warn("Email уже используется другим пользователем: " + email);
                throw new IllegalArgumentException("Email уже используется другим пользователем");
            }
            existing.setEmail(email);
            logger.info("Email обновлен для id=" + clientId + " на " + email);
        }

        if (phone != null && !phone.equals(existing.getPhone())) {
            if (!ValidationUtils.isValidPhone(phone)) {
                logger.warn("Некорректный телефон при обновлении: " + phone);
                throw new IllegalArgumentException("Неверный формат телефона");
            }
            if (isPhoneExists(phone)) {
                logger.warn("Телефон уже используется другим пользователем: " + phone);
                throw new IllegalArgumentException("Телефон уже используется другим пользователем");
            }
            existing.setPhone(phone);
            logger.info("Телефон обновлен для id=" + clientId + " на " + phone);
        }

        if (address != null && !address.equals(existing.getAddress())) {
            if (!ValidationUtils.isValidAddress(address)) {
                logger.warn("Некорректный адрес при обновлении для id=" + clientId);
                throw new IllegalArgumentException("Некорректный адрес");
            }
            existing.setAddress(address);
            logger.info("Адрес обновлен для id=" + clientId);
        }

        if (name != null && !name.equals(existing.getName())) {
            existing.setName(name);
            logger.info("Имя обновлено для id=" + clientId + " на " + name);
        }

        logger.info("Обновление данных клиента успешно для id=" + clientId);
        return existing;
    }

    // =====================
    // Смена пароля
    // =====================
    public boolean changePassword(Long clientId, String oldPassword, String newPassword) {
        logger.info("Попытка смены пароля для клиента id=" + clientId);

        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null) {
            logger.warn("Смена пароля не удалась: клиент не найден id=" + clientId);
            throw new IllegalArgumentException("Клиент не найден");
        }

        if (!client.isActive()) {
            logger.warn("Смена пароля не удалась: клиент деактивирован id=" + clientId);
            throw new IllegalStateException("Клиент деактивирован");
        }

        if (oldPassword == null || newPassword == null || oldPassword.isBlank() || newPassword.isBlank()) {
            logger.warn("Смена пароля не удалась: пустые значения паролей id=" + clientId);
            throw new IllegalArgumentException("Пароли не могут быть пустыми");
        }

        // Проверка старого пароля
        if (!PasswordUtils.checkPassword(oldPassword, client.getPasswordHash())) {
            logger.warn("Смена пароля не удалась: неверный старый пароль id=" + clientId);
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        // Проверка сложности нового пароля
        if (!ValidationUtils.isValidPassword(newPassword)) {
            logger.warn("Смена пароля не удалась: новый пароль не соответствует требованиям id=" + clientId);
            throw new IllegalArgumentException("Новый пароль не соответствует требованиям безопасности");
        }

        // Хэширование и установка нового пароля
        String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
        client.setPasswordHash(hashedNewPassword);
        logger.info("Пароль успешно изменён для клиента id=" + clientId);

        return true;
    }

    // =====================
    // Активация / деактивация
    // =====================
    @Override
    public boolean deactivate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) {
            logger.warn("Деактивация не удалась: клиент не найден id=" + clientId);
            return false;
        }
        existing.setActive(false);
        logger.info("Клиент деактивирован id=" + clientId);
        return true;
    }

    @Override
    public boolean activate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) {
            logger.warn("Активация не удалась: клиент не найден id=" + clientId);
            return false;
        }
        existing.setActive(true);
        logger.info("Клиент активирован id=" + clientId);
        return true;
    }

    // =====================
    // Получение клиентов
    // =====================
    @Override
    public Client getById(Long clientId) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client != null) {
            logger.info("Получение клиента id=" + clientId);
        } else {
            logger.warn("Клиент не найден id=" + clientId);
        }
        return client;
    }

    @Override
    public List<Client> listAll() {
        logger.info("Получение списка всех клиентов, total=" + ID_TO_CLIENT.size());
        return new ArrayList<>(ID_TO_CLIENT.values());
    }

    @Override
    public List<String> getOrderHistory(Long clientId) {
        List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
        if (history == null) {
            logger.warn("История заказов не найдена для клиента id=" + clientId);
            return new ArrayList<>();
        }
        logger.info("Получение истории заказов для клиента id=" + clientId + ", entries=" + history.size());
        return new ArrayList<>(history);
    }

    // =====================
    // Добавление записи в историю заказов
    // =====================
    public void addOrderHistoryEntry(Long clientId, String entry) {
        logger.info("Добавление записи в историю заказов для клиента id=" + clientId + ": " + entry);

        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null || !client.isActive()) {
            logger.warn("Не удалось добавить запись: клиент не существует или деактивирован id=" + clientId);
            throw new IllegalStateException("Клиент не существует или деактивирован");
        }

        List<String> history = ID_TO_ORDER_HISTORY.computeIfAbsent(clientId, k -> new ArrayList<>());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        history.add("[" + timestamp + "] " + entry);

        logger.info("Запись успешно добавлена в историю заказов для id=" + clientId);
    }

    // =====================
    // Проверки уникальности
    // =====================
    private boolean isEmailExists(String email) {
        boolean exists = ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
        if (exists) {
            logger.info("Проверка уникальности email: " + email + " уже существует");
        } else {
            logger.info("Проверка уникальности email: " + email + " свободен");
        }
        return exists;
    }

    private boolean isPhoneExists(String phone) {
        boolean exists = ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getPhone().equals(phone));
        if (exists) {
            logger.info("Проверка уникальности телефона: " + phone + " уже существует");
        } else {
            logger.info("Проверка уникальности телефона: " + phone + " свободен");
        }
        return exists;
    }
}
