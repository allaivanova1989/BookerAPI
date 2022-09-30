package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.AuthData;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;
import static utils.PropertyReader.getProperty;

public class BaseTest {

    String token;

    @BeforeSuite
    public void setUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .setBaseUri(getProperty("url"))

                .setContentType(ContentType.JSON)
                .build();
        getToken();

    }

    public String getToken() {
        AuthData authData = new AuthData(getProperty("userNameForAuth"), getProperty("passwordForAuth"));
        return token = given()
                .body(authData)
                .when()
                .post(getProperty("auth"))
                .then()
                .extract()
                .path("token");
    }

}
