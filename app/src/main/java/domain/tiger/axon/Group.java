package domain.tiger.axon;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Group {
    public int size;
    public String group_name;

    public Group(){

    }

    /*
    Sets the group name and size
     */
    public Group(String groupName){
        this.group_name = groupName;
        size = 0;
    }

    /*
    Takes the creator's id and creates a reference to a preference.
    Create a new collection in the group, and store creator's id with a field that holds a reference to the user's personal preference.
    The creator's id will act as the key and the preference reference will act as the value.

    Procedure:
        1. Check to see if group_ame is empty. This is needed to make sure that the group_name is on the FireBase database.
        2. Create a preference for the user and store it on FireBase.
        3. Create a new collection in the group and create a new document using the creator's id and add a field that contains the reference to the creator's personal preference.
        4. Increment the size and update the FireBase database
     */
    public void addCreator(String userid){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Create preference and store in group
        Preferences pref = new Preferences();
        db.collection("groups/" + group_name + "/prefs").document("pref"+(size+1)).set(pref);

        //Obtain reference to newly created reference
        DocumentReference prefRef = db.collection("groups/" + group_name + "/prefs").document("pref"+(size+1));

        //Store preference reference in group and attach userid to use a key
        Map<String, DocumentReference> prefRefMap = new HashMap<>();
        prefRefMap.put("Reference", prefRef);
        db.collection("groups/" + group_name + "/prefrefs").document(userid).set(prefRefMap);

        //Increment and update the size of the group;
        size++;
        db.collection("groups").document(group_name).update("size", size);

        //Update user information to include what groups they are in
        DocumentReference groupRef = db.collection("groups").document(group_name);
        UserGroup userGroup = new UserGroup(groupRef, group_name);
        db.collection("users").document(userid).collection("groups").document(group_name).set(userGroup);

        //Add user to group
        Map<String, String> userMap = new HashMap<>();
        userMap.put("usid", userid);
        db.collection("groups/" + group_name +"/users").document(userid).set(userMap);

        db.collection("users").document(userid).update("currentGroup", group_name);
    }

}
