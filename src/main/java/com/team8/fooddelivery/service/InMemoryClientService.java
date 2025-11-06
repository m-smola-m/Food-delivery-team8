package com.team8.fooddelivery.service;

import com.team8.fooddelivery.interfaces.ClientService;
import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.model.Cart;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryClientService implements ClientService {

    private static final AtomicLong ID_SEQ = new AtomicLong(1);
    private static final Map<Long, Client> ID_TO_CLIENT = new HashMap<>();
    private static final Map<Long, List<String>> ID_TO_ORDER_HISTORY = new HashMap<>();

    @Override
    public Client register(String name, String email, String phone, String address) {
        Long id = ID_SEQ.getAndIncrement();
        Client client = new Client(id, name, email, phone, address, Instant.now(), true, new Cart());
        ID_TO_CLIENT.put(id, client);
        ID_TO_ORDER_HISTORY.put(id, new ArrayList<>());
        return client;
    }

    @Override
    public Client update(Long clientId, String name, String email, String phone, String address) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return null;
        if (name != null) existing.setName(name);
        if (email != null) existing.setEmail(email);
        if (phone != null) existing.setPhone(phone);
        if (address != null) existing.setAddress(address);
        return existing;
    }

    @Override
    public boolean deactivate(Long clientId) {
        Client existing = ID_TO_CLIENT.get(clientId);
        if (existing == null) return false;
        existing.setIsActive(false);
        return true;
    }

    @Override
    public Client getById(Long clientId) {
        return ID_TO_CLIENT.get(clientId);
    }

    @Override
    public List<String> getOrderHistory(Long clientId) {
    List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
    if (history == null) {
        return new ArrayList<>();
    }
    return new ArrayList<>(history);
}


    @Override
    public List<Client> listAll() {
        return new ArrayList<>(ID_TO_CLIENT.values());
    }

    public void addOrderHistoryEntry(Long clientId, String entry) {
        List<String> history = ID_TO_ORDER_HISTORY.get(clientId);
    
        // если у клиента ещё нет истории — создаём новую
        if (history == null) {
            history = new ArrayList<>();
            ID_TO_ORDER_HISTORY.put(clientId, history);
        }
    
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        history.add("[" + timestamp + "] " + entry);
    }
}


