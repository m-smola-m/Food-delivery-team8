package com.team8.fooddelivery.model.shop;

public enum ShopType {
    RESTAURANT("Ресторан"),
    CAFE("Кафе"),
    FAST_FOOD("Фаст-фуд"),
    PIZZA("Пицца"),
    SUSHI("Суши"),
    BAKERY("Пекарня"),
    COFFEE("Кофейня"),
    GROCERY("Продукты"),
    PHARMACY("Аптека"),
    OTHER("Другое");

    private final String displayName;

    ShopType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
