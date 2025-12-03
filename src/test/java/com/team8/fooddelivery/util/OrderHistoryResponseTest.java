package com.team8.fooddelivery.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderHistoryResponseTest {

    @Test
    @DisplayName("Создание OrderHistoryResponse через builder")
    void testBuilder() {
        OrderHistoryResponse.Item item1 = OrderHistoryResponse.Item.builder()
                .name("Pizza")
                .quantity(2)
                .price(10.0)
                .build();

        OrderHistoryResponse.Item item2 = OrderHistoryResponse.Item.builder()
                .name("Cola")
                .quantity(1)
                .price(5.0)
                .build();

        List<OrderHistoryResponse.Item> items = Arrays.asList(item1, item2);

        OrderHistoryResponse response = OrderHistoryResponse.builder()
                .id(1L)
                .status("COMPLETED")
                .total(25.0)
                .createdAt("2023-01-01")
                .items(items)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(25.0, response.getTotal());
        assertEquals("2023-01-01", response.getCreatedAt());
        assertEquals(items, response.getItems());
    }

    @Test
    @DisplayName("Equals и hashCode для OrderHistoryResponse")
    void testEqualsAndHashCode() {
        OrderHistoryResponse response1 = OrderHistoryResponse.builder()
                .id(1L)
                .status("PENDING")
                .total(15.0)
                .createdAt("2023-01-01")
                .items(Arrays.asList())
                .build();

        OrderHistoryResponse response2 = OrderHistoryResponse.builder()
                .id(1L)
                .status("PENDING")
                .total(15.0)
                .createdAt("2023-01-01")
                .items(Arrays.asList())
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Equals и hashCode для Item")
    void testItemEqualsAndHashCode() {
        OrderHistoryResponse.Item item1 = OrderHistoryResponse.Item.builder()
                .name("Pizza")
                .quantity(2)
                .price(10.0)
                .build();

        OrderHistoryResponse.Item item2 = OrderHistoryResponse.Item.builder()
                .name("Pizza")
                .quantity(2)
                .price(10.0)
                .build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("ToString для OrderHistoryResponse")
    void testToString() {
        OrderHistoryResponse response = OrderHistoryResponse.builder()
                .id(1L)
                .status("COMPLETED")
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("status=COMPLETED"));
    }
}
