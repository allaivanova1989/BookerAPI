package testsForAPI;

import lombok.extern.log4j.Log4j2;
import models.Bookingdates;
import models.UpdateBookingData;
import models.BookingData;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.PropertyReader.getProperty;

@Log4j2
public class PositiveTest extends BaseTest {

    @Test
    public void createBooking() {

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Check if the created booking exists in the system");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(newBookingData.getFirstname()),
                        "lastname", equalTo(newBookingData.getLastname()),
                        "totalprice", equalTo(newBookingData.getTotalprice()),
                        "bookingdates.checkin", equalTo(newBookingData.getBookingdates().getCheckin()),
                        "bookingdates.checkout", equalTo(newBookingData.getBookingdates().getCheckout()),
                        "additionalneeds", equalTo(newBookingData.getAdditionalneeds()));

    }


    @Test
    public void updateBooking() {

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Update booking");

        String updatedName = faker.name().firstName();
        String updatedLastName = faker.name().lastName();
        int updatedPrice = faker.number().numberBetween(100, 600);
        String updatedCheckInDate = getProperty("checkInDateForUpdate");
        String updatedCheckOutDate = getProperty("checkOutDateForUpdate");
        String updatedWishes = "Slippers in the room";

        BookingData updateBookingData = BookingData.builder()
                .firstname(updatedName)
                .lastname(updatedLastName)
                .totalprice(updatedPrice)
                .depositpaid(true)
                .bookingdates(new Bookingdates(updatedCheckInDate, updatedCheckOutDate))
                .additionalneeds(updatedWishes)
                .build();


        given()
                .body(updateBookingData)
                .cookie("token", token)
                .when()
                .pathParam("id", bookingID)
                .put(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200);

        log.info("Check if the updated booking exists in the system");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
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

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Update booking");
        UpdateBookingData updateBookingData = new UpdateBookingData(faker.name().firstName(), faker.name().lastName());

        String updatedName = updateBookingData.getFirstname();
        String updatedLastName = updateBookingData.getLastname();

        given()
                .body(updateBookingData)
                .cookie("token", token)
                .when()
                .pathParam("id", bookingID)
                .patch(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200);

        log.info("Check if the partially updated booking exists in the system");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(updatedName),
                        "lastname", equalTo(updatedLastName),
                        "totalprice", equalTo(newBookingData.getTotalprice()),
                        "bookingdates.checkin", equalTo(newBookingData.getBookingdates().getCheckin()),
                        "bookingdates.checkout", equalTo(newBookingData.getBookingdates().getCheckout()),
                        "additionalneeds", equalTo(newBookingData.getAdditionalneeds()));

    }

    @Test
    public void deleteUser() {

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Delete a booking");
        given()
                .cookie("token", token)
                .when()
                .pathParam("id", bookingID)
                .delete(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(201);

        log.info("Checking if the booking is deleted");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
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

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for an ID");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(newBookingData.getFirstname()),
                        "lastname", equalTo(newBookingData.getLastname()),
                        "totalprice", equalTo(newBookingData.getTotalprice()),
                        "bookingdates.checkin", equalTo(newBookingData.getBookingdates().getCheckin()),
                        "bookingdates.checkout", equalTo(newBookingData.getBookingdates().getCheckout()),
                        "additionalneeds", equalTo(newBookingData.getAdditionalneeds()));
    }

    @Test
    public void getBookingByNameAndLastName() {

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for a firstname and lastname");
        given()
                .when()
                .get(getProperty("getBookingByFirstnameAndLastName"),
                        newBookingData.getFirstname(), newBookingData.getLastname())
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", hasItem(bookingID));
    }

    @Test
    public void getBookingByCheckInDateAndCheckoutDate() {

        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Checking the receipt of a booking for a checkInDate and checkOutDate");
        given()
                .when()
                .get(getProperty("getBookingByCheckinAndCheckout"),
                        newBookingData.getBookingdates().getCheckin(), newBookingData.getBookingdates().getCheckout())
                .then()
                .assertThat()
                .statusCode(200)
                .body(anyOf(hasItem(bookingID)));
    }
}
