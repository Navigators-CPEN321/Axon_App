package domain.tiger.axon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupAvailableActivity extends AppCompatActivity {

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //ListView vars
    private ListView listViewAvailableGroups;
    private ArrayList<String> availableGroupsList = new ArrayList<>();
    private AvailableGroupAdapter adapter = new AvailableGroupAdapter(availableGroupsList,this);

    //Users' groups and database groups vars
    private List<String> db_group_names = new ArrayList<String>();
    private List<String> user_group_names = new ArrayList<String>();
    private boolean userListObtained;
    private boolean groupListObtained;

    /*
    Displays drop-down menu on actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /*
    Provides functionality to drop-down menu on actionbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.invitations:
                startActivity(new Intent(GroupAvailableActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(GroupAvailableActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                auth.signOut();
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_available);

        //Set up ListView
        listViewAvailableGroups = (ListView) findViewById(R.id.listAvailableGroups);
        listViewAvailableGroups.setAdapter(adapter);

        userListObtained = false;
        groupListObtained = false;

        //Get database groups
        db.collection("groups").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                db_group_names.clear();
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    if (Integer.parseInt(qsList.get(i).get("size").toString()) < 8 &&
                            Boolean.valueOf(qsList.get(i).get("hidden").toString()) == false){
                        db_group_names.add(qsList.get(i).get("group_name").toString());
                    }
                }
                groupListObtained = true;
                if (userListObtained){
                    compareAndDisplayList();
                }
            }
        });

        //Get user groups
        db.collection("users/" + user.getUid() + "/groups").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                user_group_names.clear();
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    user_group_names.add(qsList.get(i).get("group_name").toString());
                }
                userListObtained = true;
                if (groupListObtained) {
                    compareAndDisplayList();
                }
            }
        });


    }

    /*
    Compares the users' groups and the database groups
    If the database group is not apart of the user's groups, then add to the availableGroupsList
     */
    public void compareAndDisplayList(){

        availableGroupsList.clear();
        for (int dbIt = 0; dbIt < db_group_names.size(); dbIt++){
            boolean userIsPartOfGroup = false;
            for (int userIt = 0; userIt < user_group_names.size(); userIt++){
                //Check to see if that user is in the group -> if they are then set userIsPartOfGroup flag to reflect it
                if (db_group_names.get(dbIt).equals(user_group_names.get(userIt))){
                    userIsPartOfGroup = true;
                }

            }

            //Check flag to see if they are part of the group -> if not then add them to availableGroupsList
            if (userIsPartOfGroup == false){
                availableGroupsList.add(db_group_names.get(dbIt));
            }
        }

        //Sort and notify adapter of the changes
        Collections.sort(availableGroupsList, String.CASE_INSENSITIVE_ORDER);
        adapter.notifyDataSetChanged();
    }
}
