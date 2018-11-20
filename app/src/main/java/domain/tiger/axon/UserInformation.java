package domain.tiger.axon;

import com.google.firebase.firestore.DocumentReference;

public class UserInformation {

    public String email, displayName, dobMonth, dobDay, dobYear, address, usid, currentGroup, mostRecentEventsList;

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
    }
}
