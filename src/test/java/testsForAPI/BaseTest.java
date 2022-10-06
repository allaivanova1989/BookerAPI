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
import static utils.PropertyReader.getProperty;
import static utils.PropertyReader.setProperties;

@Log4j2
public class BaseTest {

    Faker faker = new Faker();
    static String token;


    protected int createNewBookingDataAndGetBookingID() {
        log.info("Create a new booking");

        setProperties();

        BookingData bookingData = BookingData.builder()
                .firstname(getProperty("firstName"))
                .lastname(getProperty("lastName"))
                .totalprice(Integer.parseInt(getProperty("totalPrice")))
                .depositpaid(Boolean.parseBoolean(getProperty("depositpaid")))
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate")))
                .additionalneeds(getProperty("additionalneeds"))
                .build();

        return given()
                .body(bookingData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
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
                .extract()
                .path("token");

    }


}
