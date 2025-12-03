package com.team8.fooddelivery.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDtoTest {

    @Test
    @DisplayName("Создание NotificationDto через builder")
    void testBuilder() {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .clientId(2L)
                .type("INFO")
                .message("Test message")
                .timestamp("2023-01-01")
                .isRead(false)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getClientId());
        assertEquals("INFO", dto.getType());
        assertEquals("Test message", dto.getMessage());
        assertEquals("2023-01-01", dto.getTimestamp());
        assertFalse(dto.isRead());
    }

    @Test
    @DisplayName("Equals и hashCode")
    void testEqualsAndHashCode() {
        NotificationDto dto1 = NotificationDto.builder()
                .id(1L)
                .clientId(2L)
                .type("INFO")
                .message("Test")
                .timestamp("2023-01-01")
                .isRead(false)
                .build();

        NotificationDto dto2 = NotificationDto.builder()
                .id(1L)
                .clientId(2L)
                .type("INFO")
                .message("Test")
                .timestamp("2023-01-01")
                .isRead(false)
                .build();

        NotificationDto dto3 = NotificationDto.builder()
                .id(2L)
                .clientId(2L)
                .type("INFO")
                .message("Test")
                .timestamp("2023-01-01")
                .isRead(false)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("ToString")
    void testToString() {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .message("Test")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("message=Test"));
    }
}
