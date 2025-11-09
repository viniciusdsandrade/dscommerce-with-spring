package com.resftul.dscommerce.rest;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.repository.CategoryRepository;
import com.resftul.dscommerce.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.nimbusds.jose.JWSAlgorithm.HS256;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.System.nanoTime;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@TestInstance(PER_CLASS)
@DisplayNameGeneration(ReplaceUnderscores.class)
class OrderControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    JwtDecoder jwtDecoder;

    private RequestSpecification requestSpecification;

    private static final String ORDERS = "/orders";
    private static final String USERS = "/users";

    private static final byte[] TEST_SECRET =
            "test-256-bit-secret-0123456789ABCDEF0123456789AB".getBytes(StandardCharsets.UTF_8);
    private static final String ISSUER = "http://localhost/test";

    private static final String STRONG_PASSWORD = "Str0ng_P@ss!";
    private static final String DEFAULT_PHONE = "+5511999999999";
    private static final String DEFAULT_BIRTHDATE = "2000-01-01";

    @BeforeEach
    void stubJwtDecoderToUseTokenClaims() {
        when(jwtDecoder.decode(anyString())).thenAnswer(invocation -> {
            final String token = invocation.getArgument(0, String.class);

            long dotCount = token == null
                    ? 0L
                    : token.chars().filter(ch -> ch == '.').count();

            if (dotCount != 2) throw new BadJwtException("Invalid token format");

            final SignedJWT parsed;

            try {
                parsed = SignedJWT.parse(token);
            } catch (ParseException parseException) {
                throw new BadJwtException("Invalid token", parseException);
            }

            final JWTClaimsSet set;
            try {
                set = parsed.getJWTClaimsSet();
            } catch (ParseException parseException) {
                throw new BadJwtException("Invalid token claims", parseException);
            }

            final Map<String, Object> claims = new HashMap<>(set.getClaims());

            @SuppressWarnings("unchecked")
            List<String> raw = ofNullable((List<String>) claims.get("authorities"))
                    .orElseGet(() -> {
                        try {
                            return ofNullable(set.getStringListClaim("roles")).orElse(List.of());
                        } catch (Exception e) {
                            return List.of();
                        }
                    });
            final List<String> normalized = raw.stream()
                    .map(a -> a.startsWith("ROLE_") ? a : "ROLE_" + a)
                    .toList();
            claims.put("authorities", normalized);

            final Instant issuedAt = ofNullable(set.getIssueTime())
                    .map(Date::toInstant)
                    .orElseGet(Instant::now);

            final Instant expiresAt = ofNullable(set.getExpirationTime())
                    .map(Date::toInstant)
                    .orElse(issuedAt.plus(Duration.ofHours(1)));

            claims.keySet().removeAll(Set.of("iat", "exp", "nbf"));

            final var alg = parsed.getHeader().getAlgorithm();

            return Jwt.withTokenValue(token)
                    .headers(h -> {
                        if (alg != null) {
                            h.put("alg", alg.getName());
                        }
                    })
                    .subject(set.getSubject())
                    .issuer(ofNullable(set.getIssuer()).orElse(ISSUER))
                    .issuedAt(issuedAt)
                    .expiresAt(expiresAt)
                    .claims(c -> c.putAll(claims))
                    .build();
        });
    }

    @BeforeAll
    void beforeAll_init() {
        RestAssured.port = port;
        enableLoggingOfRequestAndResponseIfValidationFails();
        this.requestSpecification = new RequestSpecBuilder()
                .setPort(port)
                .setAccept(JSON)
                .build();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String issueJwt(String subjectEmail, String... authorities) {
        try {
            var now = Instant.now();
            var claims = new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .subject(subjectEmail)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(1, HOURS)))
                    .claim("authorities", List.of(authorities))
                    .build();
            var jwt = new SignedJWT(new JWSHeader(HS256), claims);
            jwt.sign(new MACSigner(TEST_SECRET));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to issue test JWT", e);
        }
    }

    private static String uniqueEmail(String prefix) {
        return "%s-%d@example.com".formatted(prefix, nanoTime());
    }

    private static String anyValidPhone() {
        return DEFAULT_PHONE;
    }

    private static String anyValidBirthDate() {
        return DEFAULT_BIRTHDATE;
    }

    private static String userPayload(String name, String email, String phone, String birthDate) {
        return """
                {
                  "name": "%s",
                  "email": "%s",
                  "phone": "%s",
                  "password": "%s",
                  "birthDate": "%s"
                }""".formatted(name, email, phone, OrderControllerRestAssuredTest.STRONG_PASSWORD, birthDate);
    }

    private void createUserAndReturnId(String name, String email) {
        String body = userPayload(
                name,
                email,
                anyValidPhone(),
                anyValidBirthDate()
        );

        given().spec(requestSpecification)
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .body("id", notNullValue())
                .body("name", equalTo(name))
                .body("email", equalToIgnoringCase(email))
                .extract()
                .jsonPath().getLong("id");
    }

    private Long createTestProductAndReturnId() {
        Category category = categoryRepository.save(new Category("Order-Test-" + nanoTime()));
        Product product = new Product(
                "Product-" + nanoTime(),
                "Order test product",
                new BigDecimal("99.90"),
                "https://example.com/order-product.jpg",
                category
        );
        return productRepository.save(product).getId();
    }

    private Long createOrderAsClientAndReturnId(String clientEmail) {
        Long productId = createTestProductAndReturnId();
        String token = issueJwt(clientEmail, "CLIENT");

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 2 }
                  ]
                }
                """.formatted(productId);

        return given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .contentType(JSON)
                .body(body)
                .when()
                .post(ORDERS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .header("Location", containsString(ORDERS + "/"))
                .body("id", notNullValue())
                .body("items.size()", equalTo(1))
                .body("items[0].productId", equalTo(productId.intValue()))
                .body("items[0].quantity", equalTo(2))
                .extract()
                .jsonPath().getLong("id");
    }

    @Test
    void GET_orders_id__ShouldReturnUnauthorized_WhenNoToken() {
        given().spec(requestSpecification)
                .pathParam("id", 1L)
                .when()
                .get(ORDERS + "/{id}")
                .then()
                .statusCode(401);
    }

    @Test
    void GET_orders_id__ShouldReturnNotFound_WhenOrderDoesNotExist() {
        String email = uniqueEmail("order-not-found");
        createUserAndReturnId("Order NotFound", email);
        String token = issueJwt(email, "CLIENT");

        given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .pathParam("id", 999_999L)
                .when()
                .get(ORDERS + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void GET_orders_id__ShouldReturnOk_WhenClientAccessesOwnOrder() {
        String email = uniqueEmail("order-owner");
        createUserAndReturnId("Owner User", email);
        Long orderId = createOrderAsClientAndReturnId(email);
        String token = issueJwt(email, "CLIENT");

        given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .pathParam("id", orderId)
                .when()
                .get(ORDERS + "/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("id", equalTo(orderId.intValue()))
                .body("items", not(empty()))
                .body("client", notNullValue());
    }

    @Test
    void GET_orders_id__ShouldReturnForbidden_WhenClientAccessesOtherUsersOrder() {
        String ownerEmail = uniqueEmail("order-owner");
        createUserAndReturnId("Owner User", ownerEmail);
        Long orderId = createOrderAsClientAndReturnId(ownerEmail);

        String strangerEmail = uniqueEmail("order-stranger");
        createUserAndReturnId("Stranger User", strangerEmail);
        String strangerToken = issueJwt(strangerEmail, "CLIENT");

        given().spec(requestSpecification)
                .header("Authorization", bearer(strangerToken))
                .pathParam("id", orderId)
                .when()
                .get(ORDERS + "/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    void GET_orders__ShouldReturnOk_WhenAdminToken() {
        String adminEmail = uniqueEmail("order-admin");
        createUserAndReturnId("Order Admin", adminEmail);

        issueJwt(adminEmail, "CLIENT");
        Long orderId = createOrderAsClientAndReturnId(adminEmail);

        String adminToken = issueJwt(adminEmail, "ADMIN");

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .when()
                .get(ORDERS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("$", notNullValue())
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("id", hasItem(orderId.intValue()));
    }

    @Test
    void GET_orders__ShouldReturnForbidden_WhenClientToken() {
        String email = uniqueEmail("order-client-forbidden");
        createUserAndReturnId("Client Forbidden", email);
        String token = issueJwt(email, "CLIENT");

        given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .when()
                .get(ORDERS)
                .then()
                .statusCode(403);
    }

    @Test
    void POST_orders__ShouldReturnCreated_WhenClientAndValidPayload() {
        String email = uniqueEmail("order-create");
        createUserAndReturnId("Order Creator", email);
        String token = issueJwt(email, "CLIENT");
        Long productId = createTestProductAndReturnId();

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 3 }
                  ]
                }
                """.formatted(productId);

        given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .contentType(JSON)
                .body(body)
                .when()
                .post(ORDERS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .header("Location", containsString(ORDERS + "/"))
                .body("id", notNullValue())
                .body("items.size()", equalTo(1))
                .body("items[0].productId", equalTo(productId.intValue()))
                .body("items[0].quantity", equalTo(3))
                .body("client", notNullValue())
                .body("payment", anyOf(nullValue(), notNullValue()))
                .body("status", notNullValue());
    }

    @Test
    void POST_orders__ShouldReturnBadRequest_WhenItemsEmpty() {
        String email = uniqueEmail("order-empty-items");
        createUserAndReturnId("Empty Items User", email);
        String token = issueJwt(email, "CLIENT");

        String body = """
                {
                  "items": [ ]
                }
                """;

        given().spec(requestSpecification)
                .header("Authorization", bearer(token))
                .contentType(JSON)
                .body(body)
                .when()
                .post(ORDERS)
                .then()
                .statusCode(400)
                .contentType(JSON);
    }

    @Test
    void POST_orders__ShouldReturnUnauthorized_WhenNoToken() {
        Long productId = createTestProductAndReturnId();

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 1 }
                  ]
                }
                """.formatted(productId);

        given().spec(requestSpecification)
                .contentType(JSON)
                .body(body)
                .when()
                .post(ORDERS)
                .then()
                .statusCode(401);
    }

    @Test
    void POST_orders__ShouldReturnForbidden_WhenAdminWithoutClientRole() {
        String email = uniqueEmail("order-admin-only");
        createUserAndReturnId("Admin Only", email);
        String adminToken = issueJwt(email, "ADMIN");
        Long productId = createTestProductAndReturnId();

        String body = """
                {
                  "items": [
                    { "productId": %d, "quantity": 1 }
                  ]
                }
                """.formatted(productId);

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .contentType(JSON)
                .body(body)
                .when()
                .post(ORDERS)
                .then()
                .statusCode(403);
    }

    @Test
    void GET_orders_id__ShouldReturnUnauthorized_WhenTokenInvalid() {
        given().spec(requestSpecification)
                .header("Authorization", "Bearer INVALID.TOKEN")
                .pathParam("id", 1L)
                .when()
                .get(ORDERS + "/{id}")
                .then()
                .statusCode(401);
    }
}

