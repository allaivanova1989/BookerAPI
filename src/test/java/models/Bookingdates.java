package models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Bookingdates {
    private String checkin;
    private String checkout;

}
