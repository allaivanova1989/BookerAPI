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

        int bookingID = createNewBookingDataAndGetBookingID();

        log.info("Check if the created booking exists in the system");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(getProperty("firstName")),
                        "lastname", equalTo(getProperty("lastName")),
                        "totalprice", equalTo(Integer.parseInt(getProperty("totalPrice"))),
                        "bookingdates.checkin", equalTo(getProperty("checkInDateForCreate")),
                        "bookingdates.checkout", equalTo(getProperty("checkOutDateForCreate")),
                        "additionalneeds", equalTo(getProperty("additionalneeds")));

    }


    @Test
    public void updateBooking() {

        int bookingID = createNewBookingDataAndGetBookingID();

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

        int bookingID = createNewBookingDataAndGetBookingID();

        log.info("Update booking");
        String updatedName = faker.name().firstName();
        String updatedLastName = faker.name().lastName();
        UpdateBookingData updateBookingData = new UpdateBookingData(updatedName, updatedLastName);

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
                        "totalprice", equalTo(Integer.parseInt(getProperty("totalPrice"))),
                        "bookingdates.checkin", equalTo(getProperty("checkInDateForCreate")),
                        "bookingdates.checkout", equalTo(getProperty("checkOutDateForCreate")),
                        "additionalneeds", equalTo(getProperty("additionalneeds")));

    }


    @Test
    public void deleteUser() {

        int bookingID = createNewBookingDataAndGetBookingID();

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

        int bookingID = createNewBookingDataAndGetBookingID();

        log.info("Checking the receipt of a booking for an ID");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo(getProperty("firstName")),
                        "lastname", equalTo(getProperty("lastName")),
                        "totalprice", equalTo(Integer.parseInt(getProperty("totalPrice"))),
                        "bookingdates.checkin", equalTo(getProperty("checkInDateForCreate")),
                        "bookingdates.checkout", equalTo(getProperty("checkOutDateForCreate")),
                        "additionalneeds", equalTo(getProperty("additionalneeds")));
    }

    @Test
    public void getBookingByNameAndLastName() {

        int bookingID = createNewBookingDataAndGetBookingID();

        log.info("Checking the receipt of a booking for a firstname and lastname");
        given()
                .when()
                .param("firstname", getProperty("firstName"))
                .param("lastname", getProperty("lastName"))
                .get(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", hasItem(bookingID));
    }

    @Test
    public void getBookingByCheckInDateAndCheckoutDate() {

        int bookingID = createNewBookingDataAndGetBookingID();

        log.info("Checking the receipt of a booking for a checkInDate and checkOutDate");
        given()
                .when()
                .param("checkin", getProperty("checkInDateForCreate"))
                .param("checkout", getProperty("checkOutDateForCreate"))
                .get(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(200)
                .body(anyOf(hasItem(bookingID)));
    }
}
