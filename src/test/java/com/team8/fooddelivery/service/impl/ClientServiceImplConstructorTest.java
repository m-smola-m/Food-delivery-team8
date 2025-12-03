package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientServiceImplConstructorTest {

    @Test
    @DisplayName("Constructor with ClientRepository and CartService should create instance")
    void testConstructorWithNotificationService() {
        ClientRepository clientRepository = new ClientRepository();
        CartServiceImpl cartService = new CartServiceImpl();
        ClientServiceImpl service = new ClientServiceImpl(clientRepository, cartService);
        assertNotNull(service);
    }
}
