package testsForAPI;

import com.github.javafaker.Faker;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import lombok.extern.log4j.Log4j2;
import models.AuthData;
import models.Bookingdates;
import models.BookingData;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static utils.PropertyReader.getProperty;

@Log4j2
public class BaseTest {

    static String token;
    BookingData newBookingData;
    int bookingID;
    Faker faker = new Faker();

    protected BookingData getNewBookingData() {
        log.info("Create a new booking");
        return newBookingData = BookingData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate")))
                .additionalneeds("TV in the room")
                .build();


    }

    protected int getBookingIDAndCheckOfCreating() {
        return given()
                .body(newBookingData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .extract()
                .path("bookingid");


    }

    @BeforeSuite
    public void setUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .setBaseUri(getProperty("url"))
                .setContentType(ContentType.JSON)
                .build();

        AuthData authData = new AuthData(getProperty("userNameForAuth"), getProperty("passwordForAuth"));

        token = given()
                .body(authData)
                .when()
                .post(getProperty("auth"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

    }


}