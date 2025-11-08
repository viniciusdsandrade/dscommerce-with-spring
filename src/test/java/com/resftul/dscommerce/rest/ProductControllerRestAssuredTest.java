package com.resftul.dscommerce.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Duration;
import java.util.*;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.resftul.dscommerce.entity.Category;
import com.resftul.dscommerce.entity.Product;
import com.resftul.dscommerce.repository.CategoryRepository;
import com.resftul.dscommerce.repository.ProductRepository;

import static com.nimbusds.jose.JWSAlgorithm.HS256;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.System.nanoTime;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.springframework.security.oauth2.jwt.BadJwtException;

import java.text.ParseException;
import java.util.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@TestInstance(PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProductControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoBean
    JwtDecoder jwtDecoder;

    private RequestSpecification requestSpecification;

    private static final String PRODUCTS = "/products";
    private static final byte[] TEST_SECRET =
            "test-256-bit-secret-0123456789ABCDEF0123456789AB".getBytes(StandardCharsets.UTF_8);
    private static final String ISSUER = "http://localhost/test";

    private String adminToken;
    private String clientToken;

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
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        this.requestSpecification = new RequestSpecBuilder().setPort(port).build();

        this.adminToken = issueJwt("maria@gmail.com", "ADMIN");
        this.clientToken = issueJwt("alex@gmail.com", "CLIENT");
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

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @BeforeEach
    void cleanDb() {
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    private Category ensureCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    private Long seedProduct(
            String name,
            BigDecimal price,
            String imgUrl,
            Category... categories
    ) {
        Product product = new Product(name, "desc-" + name, price, imgUrl, categories);
        return productRepository.save(product).getId();
    }

    private static String productPayload(
            String name,
            String description,
            BigDecimal price,
            String imgUrl,
            List<Category> categories
    ) {
        StringBuilder categoriesJson = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            categoriesJson.append("""
                        { "id": %d, "name": "%s" }
                    """.formatted(c.getId(), c.getName()));
            if (i + 1 < categories.size()) categoriesJson.append(",");
        }
        categoriesJson.append("]");

        return """
                {
                  "name": "%s",
                  "description": "%s",
                  "price": %s,
                  "imgUrl": "%s",
                  "categories": %s
                }
                """.formatted(name, description, price.toPlainString(), imgUrl, categoriesJson);
    }

    @Test
    void findAll_DeveRetornar200_E_PaginaVazia_QuandoNaoHaProdutos() {
        given().spec(requestSpecification)
                .when().get(PRODUCTS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("$", hasKey("content"))
                .body("content.size()", equalTo(0));
    }

    @Test
    void findAll_DeveRespeitarPaginacao_E_FiltroPorNome() {
        Category cat = ensureCategory("Electronics");
        seedProduct("Phone X", new BigDecimal("1999.90"), "https://img/1.jpg", cat);
        seedProduct("Notebook Pro", new BigDecimal("8999.00"), "https://img/2.jpg", cat);
        seedProduct("Phone Y", new BigDecimal("2199.00"), "https://img/3.jpg", cat);

        given().spec(requestSpecification)
                .queryParam("name", "Phone")
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when().get(PRODUCTS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("$", hasKey("content"))
                .body("content.size()", lessThanOrEqualTo(2))
                .body("content.name", everyItem(containsStringIgnoringCase("Phone")));
    }

    @Test
    void findById_DeveRetornar200_QuandoIdExiste() {
        Category category = ensureCategory("Books");
        Long id = seedProduct("Clean Code", new BigDecimal("123.45"), "https://img/clean.jpg", category);

        given().spec(requestSpecification)
                .pathParam("id", id)
                .when().get(PRODUCTS + "/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Clean Code"))
                .body("price", equalTo(123.45f))
                .body("categories", not(empty()))
                .body("categories", hasItem("Books"));
    }

    @Test
    void findById_DeveRetornar404_QuandoIdNaoExiste() {
        given().spec(requestSpecification)
                .pathParam("id", 999L)
                .when().get(PRODUCTS + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void insert_DeveRetornar201_QuandoAdminE_PayloadValido() {
        Category category = ensureCategory("Gadgets");
        String name = "Product-" + nanoTime();
        String body = productPayload(
                name,
                "descrição " + name,
                new BigDecimal("49.90"),
                "https://example.com/img.jpg",
                List.of(category));

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .contentType(JSON)
                .body(body)
                .when().post(PRODUCTS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .header("Location", containsString(PRODUCTS + "/"))
                .body("id", notNullValue())
                .body("name", equalTo(name))
                .body("categories", hasItem("Gadgets"));
    }

    @Test
    void insert_DeveRetornar400_QuandoAdminE_PayloadInvalido() {
        ensureCategory("Home");
        String body = """
                {
                  "name": "",
                  "description": "x",
                  "price": 0.00,
                  "imgUrl": "https://invalido.com/img.jpg",
                  "categories": []
                }
                """;

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .contentType(JSON)
                .body(body)
                .when().post(PRODUCTS)
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .contentType(JSON);
    }

    @Test
    void insert_DeveRetornar403_QuandoClientLogado() {
        Category category = ensureCategory("Office");
        String body = productPayload(
                "Produto-" + nanoTime(),
                "desc",
                new BigDecimal("10.00"),
                "https://ok.com/p.png",
                List.of(category));

        given().spec(requestSpecification)
                .header("Authorization", bearer(clientToken))
                .contentType(JSON)
                .body(body)
                .when().post(PRODUCTS)
                .then()
                .statusCode(403);
    }

    @Test
    void insert_DeveRetornar401_QuandoTokenInvalido() {
        Category category = ensureCategory("Kitchen");
        String body = productPayload(
                "Produto-" + nanoTime(),
                "desc",
                new BigDecimal("10.00"),
                "https://ok.com/p.png",
                List.of(category));

        given().spec(requestSpecification)
                .header("Authorization", "Bearer INVALID.TOKEN")
                .contentType(JSON)
                .body(body)
                .when().post(PRODUCTS)
                .then()
                .statusCode(401);
    }

    @Test
    void update_DeveRetornar200_QuandoAdminE_PayloadValido() {
        Category category1 = ensureCategory("Tech");
        Category category2 = ensureCategory("Sale");
        Long id = seedProduct("Old Name", new BigDecimal("100.00"), "https://img/old.jpg", category1);

        String newName = "Updated-" + nanoTime();
        String body = productPayload(
                newName,
                "nova descrição",
                new BigDecimal("99.99"),
                "https://example.com/new.jpg",
                List.of(category1, category2));

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .contentType(JSON)
                .pathParam("id", id)
                .body(body)
                .when().put(PRODUCTS + "/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo(newName))
                .body("categories", hasItems("Tech", "Sale"));
    }

    @Test
    void update_DeveRetornar404_QuandoIdNaoExiste() {
        Category category = ensureCategory("Any");
        String body = productPayload(
                "Whatever",
                "desc",
                new BigDecimal("10.00"),
                "https://ok.com/x.png",
                List.of(category));

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .contentType(JSON)
                .pathParam("id", 888_888L)
                .body(body)
                .when().put(PRODUCTS + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void delete_DeveRetornar204_QuandoAdmin() {
        Category category = ensureCategory("ToDelete");
        Long id = seedProduct("ToDelete-" + nanoTime(), new BigDecimal("1.23"), "https://img/del.jpg", category);

        given().spec(requestSpecification)
                .header("Authorization", bearer(adminToken))
                .pathParam("id", id)
                .when().delete(PRODUCTS + "/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void delete_DeveRetornar403_QuandoClient() {
        Category category = ensureCategory("Nope");
        Long id = seedProduct("Forbidden-" + nanoTime(), new BigDecimal("2.34"), "https://img/nope.jpg", category);

        given().spec(requestSpecification)
                .header("Authorization", bearer(clientToken))
                .pathParam("id", id)
                .when().delete(PRODUCTS + "/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    void delete_DeveRetornar401_QuandoTokenInvalido() {
        Category category = ensureCategory("Invalid");
        Long id = seedProduct("Unauthorized-" + nanoTime(), new BigDecimal("3.45"), "https://img/u.jpg", category);

        given().spec(requestSpecification)
                .header("Authorization", "Bearer INVALID.TOKEN")
                .pathParam("id", id)
                .when().delete(PRODUCTS + "/{id}")
                .then()
                .statusCode(401);
    }
}
