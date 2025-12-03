package com.team8.fooddelivery.util;

import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.service.ClientService;
import com.team8.fooddelivery.service.ShopInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилита для подготовки данных тестовых аккаунтов на страницах входа клиентов и магазинов.
 */
public final class LoginPageDataProvider {
    private static final Logger log = LoggerFactory.getLogger(LoginPageDataProvider.class);
    private static final int CLIENT_LIMIT = 5;
    private static final int SHOP_LIMIT = 5;
    private static final String CLIENT_TEST_DATA_PATH = "src/main/resources/sql/test_data/002_insert_clients.sql";
    private static final String SHOP_TEST_DATA_PATH = "src/main/resources/sql/test_data/003_insert_shops.sql";

    private LoginPageDataProvider() {
    }

    public static void attachClientDemoData(HttpServletRequest request, ClientService clientService) {
        try {
            List<Client> clients = clientService.listAll().stream()
                    .limit(CLIENT_LIMIT)
                    .collect(Collectors.toList());
            request.setAttribute("demoClients", clients);
        } catch (Exception e) {
            log.warn("Не удалось загрузить тестовых клиентов", e);
            request.setAttribute("demoClients", Collections.emptyList());
        }
        request.setAttribute("clientTestDataSource", CLIENT_TEST_DATA_PATH);
    }

    public static void attachShopDemoData(HttpServletRequest request, ShopInfoService shopService) {
        try {
            List<Shop> shops = shopService.getAllShops().stream()
                    .limit(SHOP_LIMIT)
                    .collect(Collectors.toList());
            request.setAttribute("demoShops", shops);
        } catch (Exception e) {
            log.warn("Не удалось загрузить тестовые магазины", e);
            request.setAttribute("demoShops", Collections.emptyList());
        }
        request.setAttribute("shopTestDataSource", SHOP_TEST_DATA_PATH);
    }
}

