package com.team8.fooddelivery.util;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class OrderHistoryResponse {
    Long id;
    String status;
    Double total;
    String createdAt;
    List<Item> items;

    @Value
    @Builder
    public static class Item {
        String name;
        Integer quantity;
        Double price;
    }
}


