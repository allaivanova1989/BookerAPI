package tests;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;
import models.Bookingdates;
import models.UserData;
import org.apache.hc.client5.http.cookie.Cookie;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.PropertyReader.getProperty;

@Log4j2
public class PositiveTest extends BaseTest {

    UserData newUserData;
    Faker faker = new Faker();

    public void create() {
        newUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates("2022-12-29", "2023-01-01"))
                .additionalneeds("TV in the room")
                .build();


    }

    @Test
    public void createBooking() {

        log.info("create new user");
        create();
        String name = newUserData.getFirstname();
        String lastName = newUserData.getLastname();
        int price = newUserData.getTotalprice();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();
        String wishes = newUserData.getAdditionalneeds();

        int bookingID = given()
                .body(newUserData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .extract()
                .path("bookingid");


        log.info("Check if the created user exists in the system");

        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(name),
                        "lastname", equalTo(lastName),
                        "totalprice", equalTo(price),
                        "bookingdates.checkin", equalTo(checkInDate),
                        "bookingdates.checkout", equalTo(checkOutDate),
                        "additionalneeds", equalTo(wishes));

    }


    @Test
    public void updateBooking() {

        log.info("create new user");
        create();

        int bookingID = given()
                .body(newUserData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .extract()
                .path("bookingid");


        log.info("update booking");

        UserData updateUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates("2022-11-29", "2023-02-01"))
                .additionalneeds("Slippers in the room")
                .build();

        String updatedName = updateUserData.getFirstname();
        String updatedLastName = updateUserData.getLastname();
        int updatedPrice = updateUserData.getTotalprice();
        String updatedCheckInDate = updateUserData.getBookingdates().getCheckin();
        String updatedCheckOutDate = updateUserData.getBookingdates().getCheckout();
        String updatedWishes = updateUserData.getAdditionalneeds();

        given()
                .body(updateUserData)
                .header("cookie", "token=" + token)
                .when()
                .put(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200);

        log.info("Check if the updated user exists in the system");

        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(updatedName),
                        "lastname", equalTo(updatedLastName),
                        "totalprice", equalTo(updatedPrice),
                        "bookingdates.checkin", equalTo(updatedCheckInDate),
                        "bookingdates.checkout", equalTo(updatedCheckOutDate),
                        "additionalneeds", equalTo(updatedWishes));


    }

    @Test
    public void deleteUser() {

        log.info("create new user");
        create();

        int bookingID = given()
                .body(newUserData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .extract()
                .path("bookingid");

        log.info("delete user");

        given()
                .header("cookie", "token=" + token)
                .when()
                .delete(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(201);

        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .statusCode(404);
    }


}
