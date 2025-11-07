package com.team8.fooddelivery.service;

import com.team8.fooddelivery.interfaces.ClientService;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.utils.PasswordUtils;

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
    @Override
    public Client register(String name, String email, String phone, String address, String password) {
        if (!isValidEmail(email)) throw new IllegalArgumentException("Неверный формат email");
        if (!isValidPhone(phone)) throw new IllegalArgumentException("Неверный формат телефона");
        if (!isValidAddress(address)) throw new IllegalArgumentException("Неверный формат адреса");
        if (!isValidPassword(password)) throw new IllegalArgumentException("Слабый пароль");

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
    public Client update(Long clientId, String name, String email, String phone, String address) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return null;

        if (name != null) existing.setName(name);
        if (email != null) {
            if (!isValidEmail(email)) throw new IllegalArgumentException("Неверный формат email");
            existing.setEmail(email);
        }
        if (phone != null) {
            if (!isValidPhone(phone)) throw new IllegalArgumentException("Неверный формат телефона");
            existing.setPhone(phone);
        }
        if (address != null) {
            if (!isValidAddress(address)) throw new IllegalArgumentException("Неверный формат адреса");
            existing.setAddress(address);
        }
        return existing;
    }

    // =====================
    // Деактивация
    // =====================
    @Override
    public boolean deactivate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return false;
        existing.setActive(false);
        return true;
    }

    // =====================
    // Активация
    // =====================
    @Override
    public boolean activate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return false;
        existing.setActive(true);
        return true;
    }


    // =====================
    // Получение клиента
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
    // Добавление заказа
    // =====================
    public void addOrderHistoryEntry(Long clientId, String entry) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null || !client.isActive()) {
            throw new IllegalStateException("Клиент не существует или деактивирован");
        }

        List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
        if (history == null) {
            history = new ArrayList<>();
            ID_TO_ORDER_HISTORY.put(clientId, history);
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        history.add("[" + timestamp + "] " + entry);
    }

    // =====================
    // Валидация
    // =====================
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^8\\d{10}$");
    }

    private boolean isValidAddress(String address) {
        if (address == null) return false;
        String[] parts = address.split(",");
        if (parts.length != 10) return false;
        try {
            Double.parseDouble(parts[7]); // latitude
            Double.parseDouble(parts[8]); // longitude
        } catch (NumberFormatException e) {
            return false;
        }
        for (int i = 0; i <= 6; i++) {
            if (parts[i].trim().isEmpty()) return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    }
}
