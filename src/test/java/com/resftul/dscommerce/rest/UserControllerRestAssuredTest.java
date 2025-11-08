package com.resftul.dscommerce.rest;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

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
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    private static final String USERS = "/users";

    private static final byte[] TEST_SECRET =
            "test-256-bit-secret-0123456789ABCDEF0123456789AB".getBytes(UTF_8);

    private static final byte[] INVALID_SECRET =
            "invalid-256-bit-secret-0123456789ABCDEF0123456789".getBytes(UTF_8);

    private static final String EXPECTED_ISSUER = "http://localhost/test";

    private static final String STRONG_PASSWORD = "Str0ng_P@ss!";
    private static final String DEFAULT_PHONE = "+5511999999999";
    private static final String DEFAULT_BIRTHDATE = "2000-01-01";

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

    private static String uniqueEmail(String prefix) {
        return "%s-%s@example.com".formatted(prefix, UUID.randomUUID());
    }

    private static String anyValidPhone() {
        return DEFAULT_PHONE;
    }

    private static String anyValidBirthDate() {
        return DEFAULT_BIRTHDATE;
    }

    private static String userPayload(String name, String email, String phone, String password, String birthDate) {
        return """
                {
                  "name": "%s",
                  "email": "%s",
                  "phone": "%s",
                  "password": "%s",
                  "birthDate": "%s"
                }""".formatted(name, email, phone, password, birthDate);
    }

    private Long createUserAndReturnId(String name, String email) {
        String body = userPayload(
                name,
                email,
                anyValidPhone(),
                STRONG_PASSWORD,
                anyValidBirthDate()
        );

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
                .body("password", nullValue())
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
                    .claim("username", usernameEmail)
                    .build();
            var jwt = new SignedJWT(new JWSHeader(HS256), claims);
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to issue test JWT", e);
        }
    }

    private Stream<Arguments> invalidTokenProvider() {
        String emailExpired = uniqueEmail("expired");
        createUserAndReturnId("Expired User", emailExpired);
        String expired = issueCustomJwt(-1, TEST_SECRET, EXPECTED_ISSUER, emailExpired);

        String emailBadSign = uniqueEmail("bad-sign");
        createUserAndReturnId("Bad Sign", emailBadSign);
        String invalidSigned = issueCustomJwt(1, INVALID_SECRET, EXPECTED_ISSUER, emailBadSign);

        String emailBadIss = uniqueEmail("bad-iss");
        createUserAndReturnId("Bad Iss", emailBadIss);
        String badIssuerToken = issueCustomJwt(1, TEST_SECRET, "http://malicious/issuer", emailBadIss);

        return Stream.of(
                Arguments.of("expired", expired),
                Arguments.of("invalid-signature", invalidSigned),
                Arguments.of("invalid-issuer", badIssuerToken)
        );
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
                .body("content[0].name", anyOf(equalTo("Zuleica"), equalTo("Zuleica ")))
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
                .statusCode(404)
                .contentType(JSON)
                .body("$", hasSize(1))
                .body("[0].errorCode", equalTo("RESOURCE_NOT_FOUND"))
                .body("[0].message", equalTo("User not found"))
                .body("[0].details", equalTo("uri=/users/999999"));
    }

    @Test
    void GET_users_id__ShouldReturnBadRequest_WhenIdNotNumeric() {
        given().spec(publicSpec)
                .when()
                .get(USERS + "/{id}", "abc")
                .then()
                .statusCode(400)
                .contentType(JSON)
                .body("$", hasSize(1))
                .body("[0].errorCode", equalTo("METHOD_ARGUMENT_TYPE_MISMATCH"))
                .body("[0].message", notNullValue())
                .body("[0].details", equalTo("uri=/users/abc"));
    }

    @Test
    void GET_users_me__ShouldReturnOk_WithValidBearerToken() {
        String me = uniqueEmail("me");

        createUserAndReturnId("Me User", me);

        String token = obtainAccessToken(me, STRONG_PASSWORD);

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

    @ParameterizedTest(name = "GET_users_me__ShouldReturnUnauthorized_WhenTokenInvalid[{0}]")
    @MethodSource("invalidTokenProvider")
    void GET_users_me__ShouldReturnUnauthorized_WhenTokenInvalid(String scenario, String token) {
        given().spec(publicSpec)
                .auth().oauth2(token)
                .when()
                .get(USERS + "/me")
                .then()
                .statusCode(401);
    }

    @Test
    void POST_users__ShouldReturnCreated_WhenValidPayload() {
        String email = uniqueEmail("create");
        String body = userPayload("Alice", email, anyValidPhone(), STRONG_PASSWORD, anyValidBirthDate());

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
                .statusCode(400)
                .contentType(JSON)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("errorCode", everyItem(equalTo("METHOD_ARGUMENT_NOT_VALID_ERROR")))
                .body("details", everyItem(equalTo("uri=/users")))
                .body("field", hasItems("name", "email", "phone", "password", "birthDate"));
    }

    @Test
    void POST_users__ShouldReturnUnsupportedMediaType_WhenContentTypeInvalid() {
        String body = userPayload(
                "X",
                "x@example.com",
                anyValidPhone(),
                STRONG_PASSWORD,
                anyValidBirthDate()
        );

        given().spec(publicSpec)
                .contentType("text/plain")
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(415)
                .contentType(JSON)
                .body("[0].errorCode", equalTo("UNSUPPORTED_MEDIA_TYPE"))
                .body("[0].message", containsString("Content-Type"))
                .body("[0].details", equalTo("uri=/users"));
    }

    @Test
    void POST_users__ShouldReturnNotAcceptable_WhenAcceptXml() {
        String body = userPayload(
                "Y",
                "y@example.com",
                anyValidPhone(),
                STRONG_PASSWORD,
                anyValidBirthDate()
        );

        given().spec(publicSpec)
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

        given().spec(publicSpec)
                .auth().none()
                .contentType(JSON)
                .body(malformed)
                .when()
                .post(USERS)
                .then()
                .statusCode(400)
                .contentType(JSON)
                .body("status", equalTo(400))
                .body("error", notNullValue())
                .body("path", equalTo("/users"));
    }

    @Test
    void POST_users__ShouldReturnConflict_WhenEmailDuplicated() {
        String duplicated = uniqueEmail("dup");
        createUserAndReturnId("Dup One", duplicated);

        String body = userPayload(
                "Dup Two",
                duplicated,
                anyValidPhone(),
                STRONG_PASSWORD,
                anyValidBirthDate()
        );

        given().spec(publicSpec)
                .auth().none()
                .contentType(JSON)
                .body(body)
                .when()
                .post(USERS)
                .then()
                .statusCode(409)
                .contentType(JSON)
                .body("status", equalTo(409))
                .body("error", notNullValue())
                .body("path", equalTo("/users"));
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
