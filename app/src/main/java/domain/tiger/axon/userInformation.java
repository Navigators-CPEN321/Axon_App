package domain.tiger.axon;

import java.util.List;

public class userInformation {

    public String email, address, dobMonth, dobDay, dobYear, usid;
    public String groups;

    public userInformation(){

    }

    public userInformation(String email, String address, String dobMonth, String dobDay, String dobYear, String usid) {
        this.email = email;
        this.address = address;
        this.dobMonth = dobMonth;
        this.dobDay = dobDay;
        this.dobYear = dobYear;
        this.usid = usid;
    }
}
