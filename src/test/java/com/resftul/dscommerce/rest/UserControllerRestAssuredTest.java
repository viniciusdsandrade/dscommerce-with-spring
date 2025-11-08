package com.resftul.dscommerce.rest;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.UUID;

import static com.nimbusds.jose.JWSAlgorithm.HS256;
import static com.resftul.dscommerce.util.TokenUtil.obtainAccessToken;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@TestPropertySource(properties = {
        "security.test.jwt.secret=test-256-bit-secret-0123456789ABCDEF0123456789AB",
        "security.test.jwt.issuer=http://localhost/test"
})
@TestInstance(PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    private static final String USERS = "/users";

    private static final byte[] TEST_SECRET =
            "test-256-bit-secret-0123456789ABCDEF0123456789AB".getBytes(UTF_8);

    private static final byte[] INVALID_SECRET =
            "invalid-256-bit-secret-0123456789ABCDEF0123456789".getBytes(UTF_8);

    private static final String EXPECTED_ISSUER = "http://localhost/test";

    private RequestSpecification publicSpec;

    @BeforeAll
    void setup() {
        RestAssured.port = port;
        enableLoggingOfRequestAndResponseIfValidationFails();
        this.publicSpec = new RequestSpecBuilder()
                .setPort(port)
                .setAccept(JSON)
                .build();
    }

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        publicSpec = new RequestSpecBuilder()
                .setPort(port)
                .build();

        new RequestSpecBuilder()
                .setPort(port)
                .build();
    }

    private static String uniqueEmail(String prefix) {
        return "%s-%s@example.com".formatted(prefix, UUID.randomUUID());
    }

    private static String anyValidPhone() {
        return "+5511999999999";
    }

    private static String anyValidBirthDate() {
        return "2000-01-01";
    }

    private Long createUserAndReturnId(String name, String email) {
        String body = """
                {
                  "name": "%s",
                  "email": "%s",
                  "phone": "%s",
                  "password": "Str0ng_P@ss!",
                  "birthDate": "%s"
                }""".formatted(name, email, anyValidPhone(), anyValidBirthDate());

        return given().spec(publicSpec)
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .header("Location", matchesPattern(".*/users/\\d+"))
                .body("id", notNullValue())
                .body("name", equalTo(name))
                .body("email", equalToIgnoringCase(email))
                .extract()
                .jsonPath().getLong("id");
    }

    private static String issueCustomJwt(long hoursValid, byte[] secret, String issuer, String usernameEmail) {
        try {
            var iat = now();
            var claims = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(usernameEmail)
                    .issueTime(Date.from(iat))
                    .expirationTime(Date.from(iat.plus(hoursValid, HOURS)))
                    .claim("username", usernameEmail) // sua UserServiceImpl usa esse claim
                    .build();
            var jwt = new SignedJWT(new JWSHeader(HS256), claims);
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to issue test JWT", e);
        }
    }

    @Test
    void GET_users__ShouldReturnOk_WithDefaultPage() {
        given().spec(publicSpec)
                .when()
                .get(USERS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("$", hasKey("content"))
                .body("page.size", notNullValue())
                .body("page.number", notNullValue())
                .body("page.totalElements", notNullValue())
                .body("page.totalPages", notNullValue());
    }


    @Test
    void GET_users__ShouldRespectPaginationParams() {
        given().spec(publicSpec)
                .queryParam("page", 0)
                .queryParam("size", 2)
                .queryParam("sort", "id,asc")
                .when()
                .get(USERS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("$", hasKey("content"))
                .body("$", hasKey("page"))
                .body("page.number", equalTo(0))
                .body("page.size", equalTo(2));
    }

    @Test
    void GET_users__SortedByNameDesc_ShouldPlace_Z_Before_A() {
        var a = uniqueEmail("alpha");
        var z = uniqueEmail("zeta");
        createUserAndReturnId("Ana", a);
        createUserAndReturnId("Zuleica", z);

        given().spec(publicSpec)
                .queryParam("sort", "name,desc")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(USERS)
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("content.size()", greaterThanOrEqualTo(2))
                .body("content[0].name", anyOf(equalTo("Zuleica"), equalTo("Zuleica "))) // tolerância a espaços
                .body("content.find { it.name == 'Ana' }.email", equalToIgnoringCase(a));
    }

    @Test
    void GET_users_id__ShouldReturnUser_WhenIdExists() {
        Long id = createUserAndReturnId("Carol", uniqueEmail("findById"));

        given().spec(publicSpec)
                .pathParam("id", id)
                .when()
                .get(USERS + "/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Carol"))
                .body("email", notNullValue());
    }

    @Test
    void GET_users_id__ShouldReturnNotFound_WhenIdDoesNotExist() {
        given().spec(publicSpec)
                .pathParam("id", 999_999L)
                .when()
                .get(USERS + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void GET_users_id__ShouldReturnBadRequest_WhenIdNotNumeric() {
        given().spec(publicSpec)
                .when()
                .get(USERS + "/{id}", "abc")
                .then()
                .statusCode(400);
    }

    @Test
    void GET_users_me__ShouldReturnOk_WithValidBearerToken() {
        String me = uniqueEmail("me");
        String rawPassword = "Str0ng_P@ss!";

        createUserAndReturnId("Me User", me);

        String token = obtainAccessToken(me, rawPassword);

        given().spec(publicSpec)
                .auth().oauth2(token)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("email", equalToIgnoringCase(me))
                .body("id", notNullValue())
                .body("name", equalTo("Me User"));
    }

    @Test
    void GET_users_me__ShouldReturnUnauthorized_WhenNoToken() {
        given().spec(publicSpec)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(401);
    }

    @Test
    void GET_users_me__ShouldReturnUnauthorized_WhenTokenExpired() {
        String me = uniqueEmail("expired");
        createUserAndReturnId("Expired User", me);
        String expired = issueCustomJwt(-1, TEST_SECRET, EXPECTED_ISSUER, me);

        given().spec(publicSpec)
                .auth().oauth2(expired)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(401);
    }

    @Test
    void GET_users_me__ShouldReturnUnauthorized_WhenSignatureInvalid() {
        String me = uniqueEmail("bad-sign");
        createUserAndReturnId("Bad Sign", me);
        String invalidSigned = issueCustomJwt(1, INVALID_SECRET, EXPECTED_ISSUER, me);

        given().spec(publicSpec)
                .auth().oauth2(invalidSigned)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(401);
    }

    @Test
    void GET_users_me__ShouldReturnUnauthorized_WhenIssuerInvalid() {
        String me = uniqueEmail("bad-iss");
        createUserAndReturnId("Bad Iss", me);
        String badIssuerToken = issueCustomJwt(1, TEST_SECRET, "http://malicious/issuer", me);

        given().spec(publicSpec)
                .auth().oauth2(badIssuerToken)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(401);
    }

    @Test
    void POST_users__ShouldReturnCreated_WhenValidPayload() {
        String email = uniqueEmail("create");
        String body = """
                {
                  "name": "Alice",
                  "email": "%s",
                  "phone": "%s",
                  "password": "Str0ng_P@ss!",
                  "birthDate": "%s"
                }""".formatted(email, anyValidPhone(), anyValidBirthDate());

        given().spec(publicSpec)
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(201)
                .contentType(JSON)
                .header("Location", matchesPattern(".*/users/\\d+"))
                .body("id", notNullValue())
                .body("name", equalTo("Alice"))
                .body("email", equalToIgnoringCase(email))
                .body("password", nullValue());
    }

    @Test
    void POST_users__ShouldReturnBadRequest_WhenInvalidPayload() {
        String body = """
                {
                  "name": "A",
                  "email": "invalid-email",
                  "phone": "123",
                  "password": "123",
                  "birthDate": "2999-01-01"
                }""";

        given().spec(publicSpec)
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(400);
    }

    @Test
    void POST_users__ShouldReturnUnsupportedMediaType_WhenContentTypeInvalid() {
        String body = """
                {"name":"X","email":"x@example.com","phone":"+5511999999999","password":"Str0ng_P@ss!","birthDate":"2000-01-01"}
                """;

        given().spec(publicSpec)
                .contentType("text/plain")
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(415);
    }

    @Test
    void POST_users__ShouldReturnNotAcceptable_WhenAcceptXml() {
        String body = """
            {"name":"Y","email":"y@example.com","phone":"+5511999999999","password":"Str0ng_P@ss!","birthDate":"2000-01-01"}
            """;

        given()
                .spec(publicSpec)
                .auth().none()
                .contentType(JSON)
                .accept("application/xml")
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(406);
    }

    @Test
    void POST_users__ShouldReturnBadRequest_WhenMalformedJson() {
        String malformed = "{ \"name\": \"X\", ";

        given()
                .spec(publicSpec)
                .auth().none()
                .contentType(JSON)
                .body(malformed)
                .when()
                .post(USERS)
                .then()
                .statusCode(400);
    }

    @Test
    void POST_users__ShouldReturnConflict_WhenEmailDuplicated() {
        String duplicated = uniqueEmail("dup");
        createUserAndReturnId("Dup One", duplicated);

        String body = """
            {
              "name": "Dup Two",
              "email": "%s",
              "phone": "%s",
              "password": "Str0ng_P@ss!",
              "birthDate": "%s"
            }""".formatted(duplicated, anyValidPhone(), anyValidBirthDate());

        given()
                .spec(publicSpec)
                .auth().none()
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(409);
    }

    @Test
    void RoundTrip__POST_then_GET_by_id() {
        String email = uniqueEmail("round");
        Long id = createUserAndReturnId("Round Trip", email);

        given().spec(publicSpec)
                .pathParam("id", id)
                .when()
                .get(USERS + "/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Round Trip"))
                .body("email", equalToIgnoringCase(email))
                .body("password", nullValue());
    }
}
