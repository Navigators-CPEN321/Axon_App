package domain.tiger.axon;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Group {
    public int size;
    public String group_name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Group(){

    }

    /*
    Sets the group name
     */
    public Group(String groupName){
        this.group_name = groupName;
        size = 0;
    }

    /*
    Takes the user's id and creates a reference to a preference.
    Then it stores userid and the preference reference into a map.
    The userid will be the key and the preference reference will be the value.

    Procedure:
        1. Check to see if groupName is empty. This is needed to make sure that the groupName is on the FireBase database.
        2. Create a preference for the user and store it on FireBase.
        3. Add the user's id and a reference to the newly created preference to the map that contains all group member userid and their associated preference references.
        4. Increment the size
     */
    public void addMember(String userid){

        //Create Preference and store in group
        Preferences pref = new Preferences();
        db.collection("groups/" + group_name + "/prefs").document("pref"+(size+1)).set(pref);

        //Obtain reference to newly created reference
        DocumentReference ref;
        ref = db.collection("groups/" + group_name + "/prefs").document("pref"+(size+1));

        Map<String, DocumentReference> prefref = new HashMap<>();
        prefref.put("Reference", ref);
        db.collection("groups/" + group_name + "/prefrefs").document(userid).set(prefref);

        size++;
        db.collection("groups").document(group_name).update("size", size);

    }

}
