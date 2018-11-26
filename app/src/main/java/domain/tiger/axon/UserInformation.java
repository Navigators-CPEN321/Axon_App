package domain.tiger.axon;


public class UserInformation {

    public String email, displayName, dobMonth, dobDay, dobYear, address, usid, currentGroup, mostRecentEventsList;
    public int invitations;

    public UserInformation(){

    }

    public UserInformation(String email, String displayName,
                           String dobMonth, String dobDay, String dobYear,
                           String address, String usid) {
        this.email = email;
        this.displayName = displayName;
        this.dobMonth = dobMonth;
        this.dobDay = dobDay;
        this.dobYear = dobYear;
        this.address = address;
        this.usid = usid;
        this.invitations = 0;
    }
}
