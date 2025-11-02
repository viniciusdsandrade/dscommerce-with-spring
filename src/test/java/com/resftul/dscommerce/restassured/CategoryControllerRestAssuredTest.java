package com.resftul.dscommerce.restassured;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.repository.CategoryRepository;


import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@TestInstance(PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String CATEGORIES = "/categories";

    private RequestSpecification requestSpecification;

    @BeforeAll
    void beforeAll_initServer() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        this.requestSpecification = new RequestSpecBuilder()
                .setPort(port)
                .build();
    }

    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void findAll_ShouldReturn_200_AndEmptyArray_WhenNoCategories() {
        given().spec(requestSpecification)
                .when()
                .get(CATEGORIES)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("size()", equalTo(0));
    }

    @Test
    void findAll_ShouldReturn_200_AndArrayOfNames_WhenCategoriesExist() {
        var names = List.of("Electronics", "Books", "Home");
        names.forEach(n -> categoryRepository.save(new Category(n)));

        given().spec(requestSpecification)
                .when()
                .get(CATEGORIES)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("size()", equalTo(names.size()))
                .body("$", containsInAnyOrder(names.toArray()))
                .body("every { it != null }", is(true));
    }
}