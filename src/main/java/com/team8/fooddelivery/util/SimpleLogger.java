package com.team8.fooddelivery.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLogger {
    private final String name;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SimpleLogger(Class<?> clazz) {
        this.name = clazz.getSimpleName();
    }

    public void info(String msg) {
        System.out.println("[INFO]  " + LocalDateTime.now().format(formatter) + " — " + msg);
    }

    public void warn(String msg) {
        System.out.println("[WARN]  " + LocalDateTime.now().format(formatter) + " — " + msg);
    }

    public void error(String msg) {
        System.err.println("[ERROR] " + LocalDateTime.now().format(formatter) + " — " + msg);
    }
}
