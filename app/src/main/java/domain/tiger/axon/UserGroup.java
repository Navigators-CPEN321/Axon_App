package domain.tiger.axon;

import com.google.firebase.firestore.DocumentReference;

public class UserGroup {
    public DocumentReference Reference;
    public String group_name;

    public UserGroup(){

    }

    public UserGroup(DocumentReference groupRef, String group_name){
        this.Reference = groupRef;
        this.group_name = group_name;
    }
}
