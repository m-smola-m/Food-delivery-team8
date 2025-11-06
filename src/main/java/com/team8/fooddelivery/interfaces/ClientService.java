package com.team8.fooddelivery.interfaces;

import com.team8.fooddelivery.model.Client;
import java.util.List;

public interface ClientService {
    Client register(String name, String email, String phone, String address);
    Client update(Long clientId, String name, String email, String phone, String address);
    boolean deactivate(Long clientId);
    Client getById(Long clientId);
    List<String> getOrderHistory(Long clientId);
    List<Client> listAll();
}


