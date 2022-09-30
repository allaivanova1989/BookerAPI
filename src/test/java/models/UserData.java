package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserData {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private boolean depositpaid;
    Bookingdates bookingdates;
       private String additionalneeds;





}
