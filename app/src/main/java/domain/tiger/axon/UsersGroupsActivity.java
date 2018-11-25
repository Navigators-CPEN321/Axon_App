package domain.tiger.axon;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listViewUserGroups;
    private ArrayList<String> userGroupsList = new ArrayList<>();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private UserGroupsAdapter adapter = new UserGroupsAdapter(userGroupsList,this);

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onResume() {

        db.collection("users").document(user.getUid()).collection("groups").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                userGroupsList.clear();
                for (int i = 0; i < qsList.size(); i++){
                    userGroupsList.add(qsList.get(i).get("group_name").toString());
                    Toast.makeText(UsersGroupsActivity.this,
                            String.valueOf(i),
                            Toast.LENGTH_SHORT).show();
                    Collections.sort(userGroupsList, String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_groups);

        listViewUserGroups = (ListView) findViewById(R.id.listViewUserGroups);
        listViewUserGroups.setAdapter(adapter);

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
