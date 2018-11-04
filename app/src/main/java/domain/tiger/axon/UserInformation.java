package domain.tiger.axon;

import com.google.firebase.firestore.DocumentReference;

public class UserInformation {

    public String email, address, dobMonth, dobDay, dobYear, usid;
    public DocumentReference userGroup;

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
