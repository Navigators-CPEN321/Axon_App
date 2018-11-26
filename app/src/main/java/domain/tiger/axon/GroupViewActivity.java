package domain.tiger.axon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

/*
Group View Page:
    Allows user to view information about the group they are in.
    Also, there are two functionalities on the page:
        1. Allows the user to go to PreferenceActivity page to update their preferences.
        2. Allows the user to go to RecListActivity page to view the recommended activities we generated for them.
 */
public class GroupViewActivity extends AppCompatActivity {

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //ListView vars
    private ListView groupMembersListView;
    private GroupViewAdapter adapter;
    private ArrayList<String> displayNameList = new ArrayList<>();

    //List vars
    private ArrayList<String> usidList = new ArrayList<>();
    private ArrayList<String> deleteUsersList = new ArrayList<>();

    //Group and user info
    private TextView group_name;
    private String currentGroup;
    private int size;
    private boolean admin;
    private String userPref;


    /*
    Displays drop-down menu on actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    /*
    Provides functionality to drop-down menu on actionbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.invite_friends:
                startActivity(new Intent(GroupViewActivity.this, GroupInviteActivity.class));
                break;
            case R.id.leave_delete_group:
                LeaveOrDeleteGroup();
                break;
            case R.id.hide_group:
                ChangeHidden();
                break;
            case R.id.invitations:
                startActivity(new Intent(GroupViewActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(GroupViewActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_group_view);

        groupMembersListView = (ListView) findViewById(R.id.listViewGroupMembers);
        adapter = new GroupViewAdapter(displayNameList, this);
        groupMembersListView.setAdapter(adapter);

        //Go to users and get the latest group they viewed "currentGroup"
        db.collection("users/").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                group_name = (TextView) findViewById(R.id.tvGroupViewGroupNameTitle);
                currentGroup = documentSnapshot.get("currentGroup").toString();
                group_name.setText(currentGroup);
                AddUsidToUsidList();

            }
        });

        //LeaveOrDeleteGroup();
        goToPreferencesPage();
        goToRecActivitiesList();
    }

    /*
    Helper function for getting all the users' display names
     */
    public void AddUsidToUsidList(){
        //Go to groups/currentGroup/users
        //Get all the documents in users
        //Go to each user and get their display name and add to list
        db.collection("groups/"+ currentGroup + "/users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                usidList.clear();
                for(int i = 0; i < qsList.size(); i++){
                    usidList.add(qsList.get(i).get("usid").toString());
                }
                AddDisplayNameToDisplayNameList();
            }
        });
    }

    public void AddDisplayNameToDisplayNameList(){
        displayNameList.clear();
        for(int i = 0; i < usidList.size(); i++){
            db.collection("users").document(usidList.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    displayNameList.add(documentSnapshot.get("displayName").toString());
                    Collections.sort(displayNameList, String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    /*
    Leaves or deletes the group depending on the user is an admin or not
     */
    public void LeaveOrDeleteGroup(){
                db.collection("groups").document(currentGroup)
                        .collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       //Get user information to check if they are admin or not
                        admin = (boolean) (documentSnapshot.get("admin"));
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

                                    db.collection("groups").document(currentGroup).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            size = Integer.parseInt(documentSnapshot.get("size").toString()) - 1;
                                            db.collection("groups").document(currentGroup).update("size", size);
                                            onBackPressed();
                                        }
                                    });
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
                                   onBackPressed();
                               }
                           });
                       }
                    }
                });
    }

    /*
    Changes whether the group is public or private
    Procedure:
    1. Check if the user is an admin
       Admin: Update the group information to be public or private
       Group member: Tell the user they can't change this setting
     */
    public void ChangeHidden(){
        //Get current group information
        db.collection("groups").document(currentGroup).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final boolean hidden = Boolean.valueOf(documentSnapshot.get("hidden").toString()); //Get group hidden value
                db.collection("groups").document(currentGroup)
                        .collection("users").document(user.getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //Check if admin
                        admin = Boolean.valueOf(documentSnapshot.get("admin").toString());

                        if (admin){
                            if (hidden){
                                //Make public
                                db.collection("groups").document(currentGroup).update("hidden", false);
                                Toast.makeText(GroupViewActivity.this,
                                        "Your group is now public for others to join.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                //Make private
                                db.collection("groups").document(currentGroup).update("hidden", true);
                                Toast.makeText(GroupViewActivity.this,
                                        "Your group is now private. Other can't see your group under available groups.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(GroupViewActivity.this,
                                    "Sorry only the group creator(admin) has the ability to do this",
                                    Toast.LENGTH_LONG).show();
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
