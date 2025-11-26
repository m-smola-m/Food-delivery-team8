package com.team8.fooddelivery.repository;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.util.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("Тесты ClientRepository")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientRepositoryTest {

    private static ClientRepository clientRepository;
    private static Long testClientId;

    @BeforeAll
    static void setUp() throws SQLException {
        // Настройка подключения
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnection.setConnectionParams(dbUrl, dbUser, dbPassword);

        // Проверка подключения
        if (!DatabaseConnection.testConnection()) {
            throw new RuntimeException("Не удалось подключиться к базе данных. Убедитесь, что PostgreSQL запущен и БД создана.");
        }

        clientRepository = new ClientRepository();
    }

    @BeforeEach
    void setUpEach() throws SQLException {
        // Очистка тестовых данных перед каждым тестом (опционально)
        // Можно добавить очистку, если нужно
    }

    @Test
    @Order(1)
    @DisplayName("Сохранение клиента в БД")
    void testSaveClient() throws SQLException {
        Address address = Address.builder()
                .country("Россия")
                .city("Москва")
                .street("Ленина")
                .building("10")
                .apartment("5")
                .latitude(55.7558)
                .longitude(37.6173)
                .build();

        Client client = Client.builder()
                .name("Тестовый Клиент")
                .phone("+79991234567")
                .email("test@example.com")
                .passwordHash("hashed_password")
                .address(address)
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .createdAt(Instant.now())
                .orderHistory(List.of())
                .build();

        testClientId = clientRepository.save(client);
        assertNotNull(testClientId, "ID клиента должен быть создан");
        assertTrue(testClientId > 0, "ID должен быть положительным числом");
    }

    @Test
    @Order(2)
    @DisplayName("Поиск клиента по ID")
    void testFindById() throws SQLException {
        assumeTrue(testClientId != null, "Тест сохранения должен пройти первым");

        Optional<Client> clientOpt = clientRepository.findById(testClientId);
        assertTrue(clientOpt.isPresent(), "Клиент должен быть найден");

        Client client = clientOpt.get();
        assertEquals("Тестовый Клиент", client.getName());
        assertEquals("+79991234567", client.getPhone());
        assertEquals("test@example.com", client.getEmail());
        assertEquals(ClientStatus.ACTIVE, client.getStatus());
    }

    @Test
    @Order(3)
    @DisplayName("Поиск клиента по телефону")
    void testFindByPhone() throws SQLException {
        assumeTrue(testClientId != null, "Тест сохранения должен пройти первым");

        Optional<Client> clientOpt = clientRepository.findByPhone("+79991234567");
        assertTrue(clientOpt.isPresent(), "Клиент должен быть найден по телефону");
        assertEquals(testClientId, clientOpt.get().getId());
    }

    @Test
    @Order(4)
    @DisplayName("Поиск клиента по email")
    void testFindByEmail() throws SQLException {
        assumeTrue(testClientId != null, "Тест сохранения должен пройти первым");

        Optional<Client> clientOpt = clientRepository.findByEmail("test@example.com");
        assertTrue(clientOpt.isPresent(), "Клиент должен быть найден по email");
        assertEquals(testClientId, clientOpt.get().getId());
    }

    @Test
    @Order(5)
    @DisplayName("Обновление клиента")
    void testUpdateClient() throws SQLException {
        assumeTrue(testClientId != null, "Тест сохранения должен пройти первым");

        Optional<Client> clientOpt = clientRepository.findById(testClientId);
        assertTrue(clientOpt.isPresent());

        Client client = clientOpt.get();
        client.setName("Обновленное Имя");
        client.setStatus(ClientStatus.UPDATED);

        clientRepository.update(client);

        Optional<Client> updatedClientOpt = clientRepository.findById(testClientId);
        assertTrue(updatedClientOpt.isPresent());
        assertEquals("Обновленное Имя", updatedClientOpt.get().getName());
        assertEquals(ClientStatus.UPDATED, updatedClientOpt.get().getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("Получение всех клиентов")
    void testFindAll() throws SQLException {
        List<Client> clients = clientRepository.findAll();
        assertNotNull(clients, "Список клиентов не должен быть null");
        assertFalse(clients.isEmpty(), "Должен быть хотя бы один клиент (тестовый)");
    }

    @Test
    @Order(7)
    @DisplayName("Удаление клиента")
    void testDeleteClient() throws SQLException {
        assumeTrue(testClientId != null, "Тест сохранения должен пройти первым");

        clientRepository.delete(testClientId);

        Optional<Client> clientOpt = clientRepository.findById(testClientId);
        assertFalse(clientOpt.isPresent(), "Клиент должен быть удален");
    }
}

