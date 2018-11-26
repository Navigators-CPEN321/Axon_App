package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersGroupsActivity extends AppCompatActivity {

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //ListView vars
    private ListView listViewUserGroups;
    private ArrayList<String> userGroupsList = new ArrayList<>();
    private UserGroupsAdapter adapter = new UserGroupsAdapter(userGroupsList,this);

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /*
    Update ListView whenever the user resume the activity in case the user joins or leaves a group.
     */
    @Override
    protected void onResume() {

        //Update ListView
        db.collection("users").document(user.getUid()).collection("groups").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                userGroupsList.clear();
                for (int i = 0; i < qsList.size(); i++){
                    userGroupsList.add(qsList.get(i).get("group_name").toString());
                    Collections.sort(userGroupsList, String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        super.onResume();
    }

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
                startActivity(new Intent(UsersGroupsActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(UsersGroupsActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_users_groups);

        listViewUserGroups = (ListView) findViewById(R.id.listViewUserGroups);
        listViewUserGroups.setAdapter(adapter);

        //Display the groups the user is in
        db.collection("users/").document(user.getUid()).collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userGroupsList.clear();
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        userGroupsList.add(doc.get("group_name").toString());
                    }
                }
                Collections.sort(userGroupsList, String.CASE_INSENSITIVE_ORDER);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
