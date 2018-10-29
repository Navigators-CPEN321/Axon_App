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
        3. Create a new collection in the group and create a new documuent using the creator's id and add a field that contains the reference to the creator's personal preference.
        4. Increment the size and update the FireBase database
     */
    public void addCreator(String userid){

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
