package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.rest.dto.CreateUserRequest;
import com.github.dagurasu.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Douglas");
        user.setAge(28);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract()
                .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should return an error when is JSON is not valid")
    @Order(2)
    public void createUserError(){
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract()
                .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void listAllUsersTest(){

        given()
            .contentType(ContentType.JSON)
            .when()
            .get(apiURL)
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));


    }
}