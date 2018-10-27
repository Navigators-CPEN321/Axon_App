package domain.tiger.axon;

public class UserInformation {

    public String email, address, dobMonth, dobDay, dobYear, usid;
    public String groups;

    public UserInformation(){

    }

    public UserInformation(String email, String address, String dobMonth, String dobDay, String dobYear, String usid) {
        this.email = email;
        this.address = address;
        this.dobMonth = dobMonth;
        this.dobDay = dobDay;
        this.dobYear = dobYear;
        this.usid = usid;
    }
}
