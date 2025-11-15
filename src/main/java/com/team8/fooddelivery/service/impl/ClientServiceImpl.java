package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.service.ClientService;
import com.team8.fooddelivery.util.PasswordUtils;
import com.team8.fooddelivery.util.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private static final AtomicLong ID_SEQ = new AtomicLong(1);
    private static final Map<Long, Client> ID_TO_CLIENT = new HashMap<>();
    private static final Map<Long, List<String>> ID_TO_ORDER_HISTORY = new HashMap<>();

    // =====================
    // Регистрация
    // =====================
    public Client register(String phone, String password, String name, String email, Address address) {
        logger.info("Попытка регистрации пользователя с телефоном: {}", phone);

        // Проверка телефона как уникального логина
        if (!ValidationUtils.isValidPhone(phone)) {
            logger.warn("Регистрация не удалась: неверный формат телефона {}", phone);
            throw new IllegalArgumentException("Неверный формат телефона");
        }
        if (isPhoneExists(phone)) {
            logger.warn("Регистрация не удалась: телефон {} уже существует", phone);
            throw new IllegalArgumentException("Пользователь с таким телефоном уже существует");
        }

        // Email можно менять, но он обязателен
        if (email == null || email.isBlank() || !ValidationUtils.isValidEmail(email)) {
            logger.warn("Регистрация не удалась: некорректный email {}", email);
            throw new IllegalArgumentException("Email некорректный");
        }

        if (password == null || password.isBlank()) {
            logger.warn("Регистрация не удалась: пустой пароль для {}", email);
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (!ValidationUtils.isValidPassword(password)) {
            logger.warn("Слабый пароль для {}", email);
            throw new IllegalArgumentException("Слабый пароль");
        }

        if (!ValidationUtils.isValidAddress(address)) {
            logger.warn("Неверный адрес для {}", email);
            throw new IllegalArgumentException("Неверный формат адреса");
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        Long id = ID_SEQ.getAndIncrement();

        Client client = new Client(id, name, phone, hashedPassword, email, address, Instant.now(), true, new Cart());

        ID_TO_CLIENT.put(id, client);
        ID_TO_ORDER_HISTORY.put(id, new ArrayList<>());

        logger.info("Пользователь зарегистрирован: {} — {}, id={}", name, phone, id);
        return client;
    }

    // =====================
// Аутентификация по телефону (телефон = логин)
// =====================
    public boolean authenticate(String phone, String password) {
        logger.info("Попытка аутентификации {}", phone);

        for (Client client : ID_TO_CLIENT.values()) {
            if (client.getPhone().equals(phone) && client.isActive()) {
                boolean ok = PasswordUtils.checkPassword(password, client.getPasswordHash());
                if (ok) logger.info("Успешная аутентификация {}", phone);
                else logger.warn("Неверный пароль {}", phone);
                return ok;
            }
        }
        logger.warn("Пользователь {} не найден или деактивирован", phone);
        return false;
    }

    // =====================
    // Обновление клиента
    // =====================
    @Override
    public Client update(Long clientId, String name, String email, Address address) {
        logger.info("Обновление данных клиента id={}", clientId);

        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) {
            logger.warn("Клиент id={} не найден", clientId);
            throw new IllegalArgumentException("Клиент не найден");
        }

        if (Objects.equals(existing.getName(), name)
                && Objects.equals(existing.getEmail(), email)
                && Objects.equals(existing.getAddress(), address)) {

            logger.warn("Изменений не обнаружено для id={}", clientId);
            throw new IllegalArgumentException("Изменений нет");
        }

        // Email можно менять
        if (email != null && !email.equals(existing.getEmail())) {
            if (!ValidationUtils.isValidEmail(email)) {
                logger.warn("Некорректный email {}", email);
                throw new IllegalArgumentException("Неверный email");
            }
            if (isEmailExists(email)) {
                logger.warn("Email {} уже используется", email);
                throw new IllegalArgumentException("Email уже используется другим пользователем");
            }
            existing.setEmail(email);
            logger.info("Email обновлен для id={} → {}", clientId, email);
        }

        if (address != null && !address.equals(existing.getAddress())) {
            if (!ValidationUtils.isValidAddress(address)) {
                logger.warn("Некорректный адрес для id={}", clientId);
                throw new IllegalArgumentException("Неверный адрес");
            }
            existing.setAddress(address);
            logger.info("Адрес обновлён для id={}", clientId);
        }

        if (name != null && !name.equals(existing.getName())) {
            existing.setName(name);
            logger.info("Имя обновлено id={} → {}", clientId, name);
        }

        logger.info("Обновление клиента id={} завершено успешно", clientId);
        return existing;
    }

    // =====================
    // Смена пароля
    // =====================
    public boolean changePassword(Long clientId, String oldPassword, String newPassword) {
        logger.info("попытка смены пароля id={}", clientId);

        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null) {
            logger.warn("Смена пароля не удалась: Клиент id={} не найден", clientId);
            throw new IllegalArgumentException("Клиент не найден");
        }

        if (!client.isActive()) {
            logger.warn("Смена пароля не удалась: Клиент id={} деактивирован", clientId);
            throw new IllegalStateException("Клиент деактивирован");
        }

        if (oldPassword == null || newPassword == null
                || oldPassword.isBlank() || newPassword.isBlank()) {
            logger.warn("Смена пароля не удалась: Пустые пароли при смене id={}", clientId);
            throw new IllegalArgumentException("Пароли не могут быть пустыми");
        }

        if (!PasswordUtils.checkPassword(oldPassword, client.getPasswordHash())) {
            logger.warn("Смена пароля не удалась: Неверный старый пароль id={}", clientId);
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        // Проверка сложности нового пароля
        if (!ValidationUtils.isValidPassword(newPassword)) {
            logger.warn("Смена пароля не удалась: Новый пароль слабый id={}", clientId);
            throw new IllegalArgumentException("Новый пароль не проходит валидацию");
        }

        // Хэширование и установка нового пароля
        String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
        client.setPasswordHash(hashedNewPassword);
        logger.info("Пароль успешно изменён для клиента id=" + clientId);

        return true;
    }

    // =====================
    // Активация/деактивация
    // =====================
    @Override
    public boolean deactivate(Long clientId) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null) {
            logger.warn("Деактивация: клиент id={} не найден", clientId);
            return false;
        }
        client.setActive(false);
        logger.info("Клиент деактивирован id={}", clientId);
        return true;
    }

    @Override
    public boolean activate(Long clientId) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null) {
            logger.warn("Активация: клиент id={} не найден", clientId);
            return false;
        }
        client.setActive(true);
        logger.info("Клиент активирован id={}", clientId);
        return true;
    }

    // =====================
    // Получение клиентов
    // =====================
    @Override
    public Client getById(Long clientId) {
        Client client = ID_TO_CLIENT.get(clientId);
        if (client != null)
            logger.info("Получение клиента id={}", clientId);
        else
            logger.warn("Клиент id={} не найден", clientId);
        return client;
    }

    @Override
    public List<Client> listAll() {
        logger.info("Получение всех клиентов. total={}", ID_TO_CLIENT.size());
        return new ArrayList<>(ID_TO_CLIENT.values());
    }

    @Override
    public List<String> getOrderHistory(Long clientId) {
        List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
        if (history == null) {
            logger.warn("История заказов пустая id={}", clientId);
            return new ArrayList<>();
        }
        logger.info("История заказов id={}, entries={}", clientId, history.size());
        return new ArrayList<>(history);
    }

    // =====================
    // Добавление записи в историю заказов
    // =====================
    public void addOrderHistoryEntry(Long clientId, String entry) {
        logger.info("Добавление записи в историю id={}, entry={}", clientId, entry);

        Client client = ID_TO_CLIENT.get(clientId);
        if (client == null || !client.isActive()) {
            logger.warn("Добавление записи не удалось: id={} отсутствует или деактивирован", clientId);
            throw new IllegalStateException("Клиент не найден или деактивирован");
        }

        List<String> history = ID_TO_ORDER_HISTORY.computeIfAbsent(clientId, k -> new ArrayList<>());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        history.add("[" + timestamp + "] " + entry);

        logger.info("Запись добавлена id={}", clientId);
    }

    // =====================
    // Проверка уникальности
    // =====================
    private boolean isEmailExists(String email) {
        boolean exists = ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));

        logger.info("Проверка email '{}': {}", email, exists ? "занят" : "свободен");
        return exists;
    }

    private boolean isPhoneExists(String phone) {
        boolean existing = ID_TO_CLIENT.values().stream()
                .anyMatch(c -> c.getPhone().equals(phone));

        logger.info("Проверка телефона '{}': {}", phone, existing ? "занят" : "свободен");
        return existing;
    }
}
