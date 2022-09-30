package tests;

import lombok.extern.log4j.Log4j2;
import models.Bookingdates;
import models.UserData;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.PropertyReader.getProperty;

@Log4j2
public class NegativeTest extends BaseTest{


    private void checkBadRequest() {
        given()
                .body(newUserData)
                .when()
                .post(getProperty("booking"))
                .then()
                .assertThat()
                .statusCode(400);


    }
    @Test
    public void checkDeletionWithoutAuth(){
       log.info("Checking if a booking can't be deleted without authentication");
        create();

        bookingID = gettingBookingIDAndCheckingOfCreating();
        given()

                .when()
                .delete(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(403)
                .body(equalTo("Forbidden"));

        log.info("Check if the booking is still in the system");

        given()
                .when()
                .get(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void checkUpdatingWithoutAuth(){
        log.info("Checking if a booking can't be updated without authentication");
        create();
        String name = newUserData.getFirstname();
        String lastName = newUserData.getLastname();
        int price = newUserData.getTotalprice();
        String checkInDate = newUserData.getBookingdates().getCheckin();
        String checkOutDate = newUserData.getBookingdates().getCheckout();
        String wishes = newUserData.getAdditionalneeds();

        bookingID = gettingBookingIDAndCheckingOfCreating();
        log.info("Update booking");

        UserData updateUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForUpdate"), getProperty("checkOutDateForUpdate")))
                .additionalneeds("Slippers in the room")
                .build();

        given()

                .when()
                .put(getProperty("booking") + "/" + bookingID)
                .then()
                .assertThat()
                .statusCode(403)
                .body(equalTo("Forbidden"));


        log.info("Verify that no change has occurred");

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
    public void checkCteatBookingWithNegativePrice(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(-200)
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate")))
                .additionalneeds("TV in the room")
                .build();

        checkBadRequest();


    }

    @Test
    public void checkCteatBookingWithOldCheckInDate(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates("2021-12-12", getProperty("checkOutDateForCreate")))
                .additionalneeds("TV in the room")
                .build();

        checkBadRequest();


    }

    @Test
    public void checkCteatBookingWithOldCheckOutDate(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), "2022-01-02"))
                .additionalneeds("TV in the room")
                .build();

        checkBadRequest();


    }

    @Test
    public void checkCteatBookingWithEmptyFields(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname("")
                .lastname("")
                .totalprice(0)
                .depositpaid(true)
                .bookingdates(new Bookingdates("", ""))
                .additionalneeds("")
                .build();

        checkBadRequest();


    }

    @Test
    public void checkCteatBookingWithNumbersInFirstName(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname("1458484554")
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate")))
                .additionalneeds("TV in the room")
                .build();

        checkBadRequest();


    }

    @Test
    public void checkCteatBookingWithNumbersInLastName(){
        log.info("create a new booking");
        newUserData = UserData.builder()
                .firstname(faker.name().firstName())
                .lastname("45464654654")
                .totalprice(faker.number().numberBetween(100, 600))
                .depositpaid(true)
                .bookingdates(new Bookingdates(getProperty("checkInDateForCreate"), getProperty("checkOutDateForCreate")))
                .additionalneeds("TV in the room")
                .build();

        checkBadRequest();


    }


    @Test
    public void getBookingByUnrealID (){

        log.info("Checking the receipt of a booking for a non-existent ID");

        given()
                .when()
                .get(getProperty("booking") + "/" + -1)
                .then()
                .assertThat()
                .statusCode(404);
    }

}
