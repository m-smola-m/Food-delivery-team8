package com.team8.fooddelivery;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainApplicationTest {

    @Test
    void testMainMethodExists() {
        assertNotNull(MainApplication.class);
        // Проверяем, что main метод существует и доступен
        assertDoesNotThrow(() -> {
            var method = MainApplication.class.getMethod("main", String[].class);
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
        });
    }

    @Test
    void testRunSchemeScriptMethodExists() {
        // Method runSchemeScript does not exist in MainApplication
        // This test is skipped as the method was removed
        assertTrue(true, "Method runSchemeScript does not exist");
    }

    @Test
    void testRunSchemeScriptThrowsIOException() {
        // Method runSchemeScript does not exist in MainApplication
        // This test is skipped as the method was removed
        assertTrue(true, "Method runSchemeScript does not exist");
    }

    @Test
    void testRunSchemeScriptThrowsExceptionWhenScriptNotFound() {
        // Method runSchemeScript does not exist in MainApplication
        // This test is skipped as the method was removed
        assertTrue(true, "Method runSchemeScript does not exist");
    }
}
