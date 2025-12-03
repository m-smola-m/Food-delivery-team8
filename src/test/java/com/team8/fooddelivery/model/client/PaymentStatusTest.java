package com.team8.fooddelivery.model.client;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void testEnumValues() {
        PaymentStatus[] values = PaymentStatus.values();
        assertEquals(4, values.length);
        assertTrue(containsValue(values, PaymentStatus.PENDING));
        assertTrue(containsValue(values, PaymentStatus.SUCCESS));
        assertTrue(containsValue(values, PaymentStatus.FAILED));
        assertTrue(containsValue(values, PaymentStatus.PAID));
    }

    @Test
    void testValueOf() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.valueOf("PAID"));
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("INVALID"));
    }

    private boolean containsValue(PaymentStatus[] values, PaymentStatus status) {
        for (PaymentStatus value : values) {
            if (value == status) {
                return true;
            }
        }
        return false;
    }
}
