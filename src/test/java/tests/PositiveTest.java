package tests;

import lombok.extern.log4j.Log4j2;
import models.Bookingdates;
import models.UserData;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.PropertyReader.getProperty;

@Log4j2
public class PositiveTest extends BaseTest {

    @Test
    public void createBooking() {

        create();
        String name = newUserData.getFirstname();
        String lastName = newUserData.getLastname();
        int price = newUserData.getTotalprice();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();
        String wishes = newUserData.getAdditionalneeds();

        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Check if the created booking exists in the system");
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

        create();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Update booking");
        UserData updateUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForUpdate"), getProperty("checkOutDateForUpdate")))
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

        log.info("Check if the updated booking exists in the system");
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
    public void partialUpdateBooking() {

        create();
        int price = newUserData.getTotalprice();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();
        String wishes = newUserData.getAdditionalneeds();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Update booking");
        UserData updateUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .build();

        String updatedName = updateUserData.getFirstname();
        String updatedLastName = updateUserData.getLastname();

        given()
                .body(updateUserData)
                .header("cookie", "token=" + token)
                .when()
                .patch(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200);

        log.info("Check if the partially updated booking exists in the system");
        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(updatedName),
                        "lastname", equalTo(updatedLastName),
                        "totalprice", equalTo(price),
                        "bookingdates.checkin", equalTo(checkInDate),
                        "bookingdates.checkout", equalTo(checkOutDate),
                        "additionalneeds", equalTo(wishes));

    }

    @Test
    public void deleteUser() {

        create();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Delete a booking");
        given()
                .header("cookie", "token=" + token)
                .when()
                .delete(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(201);

        log.info("Checking if the booking is deleted");
        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .statusCode(404);
    }


    @Test
    public void getAllBooking() {

        log.info("Getting all booking");
        given()
                .when()
                .get(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body(not(emptyArray()));

    }

    @Test
    public void getBookingByID() {

        create();
        String name = newUserData.getFirstname();
        String lastName = newUserData.getLastname();
        int price = newUserData.getTotalprice();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();
        String wishes = newUserData.getAdditionalneeds();

        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for an ID");
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
    public void getBookingByNameAndLastName() {

        create();
        String name = newUserData.getFirstname();
        String lastName = newUserData.getLastname();

        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for a firstname and lastname");
        given()
                .when()
                .get(getProperty("booking") + "?firstname=" + name + "&lastname=" + lastName)
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", hasItem(bookingID));
    }

    @Test
    public void getBookingByCheckInDateAndCheckoutDate() {

        create();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();

        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for a checkInDate and checkOutDate");
        given()
                .when()
                .get(getProperty("booking") + "?checkin=" + checkInDate + "&checkout=" + checkOutDate)
                .then()
                .assertThat()
                .statusCode(200)
                .body(anyOf(hasItem(bookingID)));
    }
}
