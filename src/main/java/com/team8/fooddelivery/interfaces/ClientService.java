package com.team8.fooddelivery.interfaces;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.Client;
import java.util.List;

public interface ClientService {
    Client register(String name, String email, String phone, Address address, String password);
    Client update(Long clientId, String name, String email, String phone, Address address);
    boolean deactivate(Long clientId);
    boolean activate(Long clientId);
    Client getById(Long clientId);
    List<String> getOrderHistory(Long clientId);
    List<Client> listAll();
    void addOrderHistoryEntry(Long clientId, String entry);
    boolean authenticate(String email, String password);
}


