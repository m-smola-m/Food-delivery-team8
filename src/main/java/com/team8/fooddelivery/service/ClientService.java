package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.AuthResponse;
import com.team8.fooddelivery.model.client.Client;

import java.util.List;

public interface ClientService {

    // Регистрация
    Client register(String phone, String password, String name, String email, Address address);

    // Логин с JWT
    AuthResponse login(String login, String password);

    // Проверка логина/пароля
    boolean authenticate(String login, String password);

    // Обновление профиля
    Client update(Long clientId, String name, String email, Address address);

    // Смена пароля
    boolean changePassword(Long clientId, String oldPassword, String newPassword);

    // Деактивация / активация
    boolean deactivate(Long clientId);
    boolean activate(Long clientId);

    // Получение клиента
    Client getById(Long clientId);
    Client getByPhone(String phone);
    Client getByEmail(String email);

    // История заказов
    List<String> getOrderHistory(Long clientId);
    void addOrderHistoryEntry(Long clientId, String entry);

    // Все клиенты
    List<Client> listAll();
}
