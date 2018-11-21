package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Group View Page:
    Allows user to view information about the group they are in.
    Also, there are two functionalities on the page:
        1. Allows the user to go to PreferenceActivity page to update their preferences.
        2. Allows the user to go to RecListActivity page to view the recommended activities we generated for them.
 */
public class GroupViewActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private ListView groupMembersListView;
    private String currentGroup;
    private ArrayList<String> usidList = new ArrayList<>();
    private ArrayList<String> displayNameList = new ArrayList<>();
    private ArrayList<String> deleteUsersList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private TextView group_name;
    private boolean admin;
    private String userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        group_name = (TextView) findViewById(R.id.tvGroupName);

        groupMembersListView = (ListView) findViewById(R.id.listViewGroupMembers);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayNameList);
        groupMembersListView.setAdapter(adapter);

        //Go to users and get the latest group they viewed "currentGroup"
        db.collection("users/").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentGroup = documentSnapshot.get("currentGroup").toString();
                /*Toast.makeText(GroupViewActivity.this,
                        currentGroup,
                        Toast.LENGTH_LONG).show();*/
                group_name.setText(currentGroup);
                AddUsidToUsidList();

            }
        });

        LeaveOrDeleteGroup();
        goToPreferencesPage();
        goToRecActivitiesList();
    }


    public void AddUsidToUsidList(){
        //Go to groups/currentGroup/users
        //Get all the documents in users
        //Go to each user and get their display name and add to list
        db.collection("groups/"+ currentGroup + "/users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                usidList.clear();
                /*Toast.makeText(GroupViewActivity.this,
                        String.valueOf(queryDocumentSnapshots.getDocuments().size()),
                        Toast.LENGTH_LONG).show();*/
                for(int i = 0; i < qsList.size(); i++){
                    usidList.add(qsList.get(i).get("usid").toString());
                    /*Toast.makeText(GroupViewActivity.this,
                            usidList.get(i),
                            Toast.LENGTH_LONG).show();*/
                }
                AddDisplayNameToDisplayNameList();
            }
        });
    }

    public void AddDisplayNameToDisplayNameList(){
        displayNameList.clear();
        /*Toast.makeText(GroupViewActivity.this,
                "Checkpoint 1",
                Toast.LENGTH_LONG).show();*/
        for(int i = 0; i < usidList.size(); i++){
            /*Toast.makeText(GroupViewActivity.this,
                    "Checkpoint 2",
                    Toast.LENGTH_LONG).show();*/
            db.collection("users").document(usidList.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    displayNameList.add(documentSnapshot.get("displayName").toString());
                    /*Toast.makeText(GroupViewActivity.this,
                            documentSnapshot.get("displayName").toString(),
                            Toast.LENGTH_LONG).show();*/
                    Collections.sort(displayNameList, String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void LeaveOrDeleteGroup(){
        Button btnLeaveDeleteGroup = (Button) findViewById(R.id.btnLeaveOrDeleteGroup);

        btnLeaveDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("groups").document(currentGroup)
                        .collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       admin = (boolean) (documentSnapshot.get("admin"));
                       Toast.makeText(GroupViewActivity.this, String.valueOf(admin), Toast.LENGTH_LONG).show();
                       if (!admin){
                            db.collection("groups").document(currentGroup)
                                    .collection("prefrefs").document(user.getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    userPref = documentSnapshot.get("prefRef").toString();
                                    userPref = userPref.substring(userPref.length() - 5, userPref.length());
                                    System.out.println(userPref);

                                    //Delete pref
                                    db.collection("groups").document(currentGroup)
                                            .collection("prefs").document(userPref).delete();

                                    //Update free pref map
                                    db.collection("groups").document(currentGroup).update(userPref, false);

                                    //Delete prefref
                                    db.collection("groups").document(currentGroup)
                                            .collection("prefrefs").document(user.getUid()).delete();

                                    //Delete user from group information
                                    db.collection("groups").document(currentGroup)
                                            .collection("users").document(user.getUid()).delete();

                                    //Delete group from user information
                                    db.collection("users").document(user.getUid()).collection("groups").document(currentGroup).delete();

                                    //Update currentGroup to be null
                                    db.collection("users").document(user.getUid()).update("currentGroup", null);
                                }
                            });
                       } else {
                           db.collection("groups").document(currentGroup)
                                   .collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                               @Override
                               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                   List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                                   deleteUsersList.clear();
                                   for (int i = 0; i < qsList.size(); i++){
                                       //Update all the users' information
                                       db.collection("users").document(qsList.get(i).get("usid").toString())
                                               .collection("groups").document(currentGroup).delete();
                                       //Delete all the prefrefs in the group
                                       db.collection("groups").document(currentGroup)
                                               .collection("prefrefs").document(qsList.get(i).get("usid").toString()).delete();
                                       //Delete all the prefs in the group
                                       db.collection("groups").document(currentGroup)
                                               .collection("prefs").document("pref"+(i+1)).delete();
                                       //Delete all the users in the group
                                       db.collection("groups").document(currentGroup)
                                               .collection("users").document(qsList.get(i).get("usid").toString()).delete();
                                   }
                                   db.collection("groups").document(currentGroup).delete();
                               }
                           });
                       }
                    }
                });
            }
        });
    }

    public void goToPreferencesPage(){
        Button btnRecommendations= findViewById(R.id.btnRecommendations);

        btnRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupViewActivity.this, PreferenceActivity.class));
            }
        });
    }


    public void goToRecActivitiesList(){
        Button btnActivities = findViewById(R.id.btnActivities);

        btnActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupViewActivity.this, RecListActivity.class));
            }
        });
    }
}
