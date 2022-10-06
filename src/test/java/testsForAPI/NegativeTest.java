package testsForAPI;

import lombok.extern.log4j.Log4j2;
import models.Bookingdates;
import models.BookingData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.PropertyReader.getProperty;

@Log4j2
public class NegativeTest extends BaseTest {

    private void checkBadRequest() {
        given()
                .body(newBookingData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(400);

    }

    @Test
    public void checkDeletionWithoutAuth() {
        log.info("Checking if a booking can't be deleted without authentication");
        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();
        given()

                .when()
                .pathParam("id", bookingID)
                .delete(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(403)
                .body(equalTo("Forbidden"));

        log.info("Check if the booking is still in the system");
        given()
                .when()
                .pathParam("id", bookingID)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void checkUpdatingWithoutAuth() {
        log.info("Checking if a booking can't be updated without authentication");
        getNewBookingData();
        bookingID = getBookingIDAndCheckOfCreating();

        log.info("Update booking");
        BookingData updateBookingData = BookingData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForUpdate"), getProperty("checkOutDateForUpdate")))
                .additionalneeds("Slippers in the room")
                .build();

        given()
                .when()
                .pathParam("id", bookingID)
                .put(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(403)
                .body(equalTo("Forbidden"));


        log.info("Verify that no change has occurred");
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

    @DataProvider(name = "differentBookingData")
    public Object[][] differentBookingData() {
        return new Object[][]{
                {faker.name().firstName(), faker.name().lastName(), -200, true, getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate"), "TV in the room"},
                {faker.name().firstName(), faker.name().lastName(), -200, true, "2021-12-12", getProperty("checkOutDateForCreate"), "TV in the room"},
                {faker.name().firstName(), faker.name().lastName(), -200, true, getProperty("checkInDateForCreate"), "2022-01-02", "TV in the room"},
                {"1458484554", faker.name().lastName(), -200, true, getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate"), "TV in the room"},
                {faker.name().firstName(), "45464654654", -200, true, getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate"), "TV in the room"},
                {"", "", 0, false, "", "", ""},
        };
    }


    @Test(dataProvider = "differentBookingData")
    public void checkCreateBookingWithIncorrectData(String name, String lastName, int price, boolean deposit, String checkin, String checkout, String wish) {
        log.info("Create a new booking with incorrect data");
        newBookingData = BookingData.builder()
                .firstname(name)
                .lastname(lastName)
                .totalprice(price)
                .depositpaid(deposit)
                .bookingdates(new Bookingdates(checkin, checkout))
                .additionalneeds(wish)
                .build();

        checkBadRequest();

    }

    @Test
    public void getBookingByUnrealID() {
        log.info("Checking the receipt of a booking for a non-existent ID");
        given()
                .when()
                .pathParam("id", -1)
                .get(getProperty("bookingWithID"))
                .then()
                .assertThat()
                .statusCode(404);
    }

}
