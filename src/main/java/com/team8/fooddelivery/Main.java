package com.team8.fooddelivery;

import com.team8.fooddelivery.model.Client;
import com.team8.fooddelivery.service.InMemoryClientService;

public class Main {
    public static void main(String[] args) {
        InMemoryClientService clientService = new InMemoryClientService();

        Client c1 = clientService.register("Иван Иванов", "ivan@example.com", "+79991112233", "Москва, Тверская 1");
        Client c2 = clientService.register("Мария Петрова", "maria@example.com", "+79995556677", "СПб, Невский 10");

        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1001: суши, 1250₽");
        clientService.addOrderHistoryEntry(c1.getId(), "Заказ #1015: пицца, 780₽");

        clientService.update(c2.getId(), null, "masha@example.com", null, null);
        clientService.deactivate(c2.getId());

        System.out.println("Клиенты:");
        clientService.listAll().forEach(System.out::println);

        System.out.println("\nИстория заказов клиента " + c1.getName() + ":");
        clientService.getOrderHistory(c1.getId()).forEach(System.out::println);
    }
}


