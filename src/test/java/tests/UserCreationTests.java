package tests;

import api.ApiClient;
import data.DataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserCreationTests {

    private ApiClient apiClient;
    private String createdUserToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            apiClient.deleteUser(createdUserToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создания пользователя с уникальными данными")
    public void testCreateUniqueUser() {
        User user = DataFactory.createUniqueUser();
        Response response = apiClient.createUser(user);
        response.then().statusCode(SC_OK);
        createdUserToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверка ошибки при попытке создания пользователя, который уже существует")
    public void testCreateDuplicateUser() {
        User user = DataFactory.createUniqueUser();
        apiClient.createUser(user);
        Response response = apiClient.createUser(user);
        response.then().statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при попытке создать пользователя без email")
    public void testCreateUserWithoutEmail() {
        User user = new User(null, "password123", "TestUser");
        Response response = apiClient.createUser(user);
        response.then().statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка ошибки при попытке создать пользователя без пароля")
    public void testCreateUserWithoutPassword() {
        User user = new User("test@example.com", null, "TestUser");
        Response response = apiClient.createUser(user);
        response.then().statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка ошибки при попытке создать пользователя без имени")
    public void testCreateUserWithoutName() {
        User user = new User("test@example.com", "password123", null);
        Response response = apiClient.createUser(user);
        response.then().statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
