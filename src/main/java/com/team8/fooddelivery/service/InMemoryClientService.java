package com.team8.fooddelivery.service;

import com.team8.fooddelivery.interfaces.ClientService;
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.utils.PasswordUtils;
import com.team8.fooddelivery.utils.ValidationUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryClientService implements ClientService {

    private static final AtomicLong ID_SEQ = new AtomicLong(1);
    private static final Map<Long, Client> ID_TO_CLIENT = new HashMap<>();
    private static final Map<Long, List<String>> ID_TO_ORDER_HISTORY = new HashMap<>();

    // =====================
    // Регистрация
    // =====================
    public Client register(String name, String email, String phone, Address address, String password) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email не может быть пустым");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Пароль не может быть пустым");

        if (!ValidationUtils.isValidEmail(email)) throw new IllegalArgumentException("Неверный формат email");
        if (!ValidationUtils.isValidPhone(phone)) throw new IllegalArgumentException("Неверный формат телефона");
        if (!ValidationUtils.isValidAddress(address)) throw new IllegalArgumentException("Неверный формат адреса");
        if (!ValidationUtils.isValidPassword(password)) throw new IllegalArgumentException("Слабый пароль");

        // Проверка уникальности email и телефона
        if (isEmailExists(email)) throw new IllegalArgumentException("Пользователь с таким email уже существует");
        if (isPhoneExists(phone)) throw new IllegalArgumentException("Пользователь с таким телефоном уже существует");

        String hashedPassword = PasswordUtils.hashPassword(password);
        Long id = ID_SEQ.getAndIncrement();

        Client client = new Client(id, name, email, phone, address, hashedPassword, Instant.now(), true, new Cart());

        ID_TO_CLIENT.put(id, client);
        ID_TO_ORDER_HISTORY.put(id, new ArrayList<>());
        return client;
    }

    // =====================
    // Аутентификация
    // =====================
    public boolean authenticate(String email, String password) {
        for (Client client : ID_TO_CLIENT.values()) {
            if (client.getEmail().equals(email) && client.isActive()) {
                return PasswordUtils.checkPassword(password, client.getPasswordHash());
            }
        }
        return false;
    }

    // =====================
    // Обновление клиента
    // =====================
    @Override
    public Client update(Long clientId, String name, String email, String phone, Address address) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) throw new IllegalArgumentException("Клиент не найден");

        // Проверка — изменилось ли хоть что-то
        if (Objects.equals(existing.getName(), name)
                && Objects.equals(existing.getEmail(), email)
                && Objects.equals(existing.getPhone(), phone)
                && Objects.equals(existing.getAddress(), address)) {
            throw new IllegalArgumentException("Изменений не обнаружено");
        }

        // Проверки и установка новых данных
        if (email != null && !email.equals(existing.getEmail())) {
            if (!ValidationUtils.isValidEmail(email)) throw new IllegalArgumentException("Неверный формат email");
            if (isEmailExists(email)) throw new IllegalArgumentException("Email уже используется другим пользователем");
            existing.setEmail(email);
        }

        if (phone != null && !phone.equals(existing.getPhone())) {
            if (!ValidationUtils.isValidPhone(phone)) throw new IllegalArgumentException("Неверный формат телефона");
            if (isPhoneExists(phone)) throw new IllegalArgumentException("Телефон уже используется другим пользователем");
            existing.setPhone(phone);
        }

        if (address != null && !address.equals(existing.getAddress())) {
            if (!ValidationUtils.isValidAddress(address)) throw new IllegalArgumentException("Некорректный адрес");
            existing.setAddress(address);
        }

        if (name != null && !name.equals(existing.getName())) {
            existing.setName(name);
        }

        return existing;
    }

    // =====================
    // Активация / деактивация
    // =====================
    @Override
    public boolean deactivate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return false;
        existing.setActive(false);
        return true;
    }

    @Override
    public boolean activate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return false;
        existing.setActive(true);
        return true;
    }

    // =====================
    // Получение клиентов
    // =====================
    @Override
    public Client getById(Long clientId) {
        return ID_TO_CLIENT.get(clientId);
    }

    @Override
    public List<Client> listAll() {
        return new ArrayList<>(ID_TO_CLIENT.values());
    }

    @Override
    public List<String> getOrderHistory(Long clientId) {
        List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
        return history == null ? new ArrayList<>() : new ArrayList<>(history);
    }

    // =====================
    // Добавление записи в историю заказов
    // =====================
    public void addOrderHistoryEntry(Long clientId, String entry) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null || !client.isActive()) {
            throw new IllegalStateException("Клиент не существует или деактивирован");
        }

        List<String> history = ID_TO_ORDER_HISTORY.computeIfAbsent(clientId, k -> new ArrayList<>());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        history.add("[" + timestamp + "] " + entry);
    }

    // =====================
    // Проверки уникальности
    // =====================
    private boolean isEmailExists(String email) {
        return ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    private boolean isPhoneExists(String phone) {
        return ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getPhone().equals(phone));
    }
}
