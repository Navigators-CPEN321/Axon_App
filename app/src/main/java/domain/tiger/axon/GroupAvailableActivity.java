package domain.tiger.axon;

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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class GroupAvailableActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listViewAvailableGroups;
    private ArrayList<String> availableGroupsList = new ArrayList<>();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private ArrayList<String> db_group_names = new ArrayList<String>();
    private ArrayList<String> user_group_names = new ArrayList<String>();
    private AvailableGroupAdapter adapter = new AvailableGroupAdapter(availableGroupsList,this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_available);

        listViewAvailableGroups = (ListView) findViewById(R.id.listAvailableGroups);

        listViewAvailableGroups.setAdapter(adapter);

        db.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //Iterate through every group in the database
                    for(DocumentSnapshot doc : task.getResult()){
                        db_group_names.add(doc.get("group_name").toString());
                    }
                    /*Toast.makeText(GroupAvailableActivity.this,
                            String.valueOf(db_group_names.size()),
                            Toast.LENGTH_LONG).show();*/
                    compareAndDisplayList(db_group_names, user_group_names);
                }

            }
        });

        db.collection("users/" + user.getUid()+ "/groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //Iterate through every group the user is part of
                    for(DocumentSnapshot doc : task.getResult()){
                        user_group_names.add(doc.get("group_name").toString());
                    }
                    /*Toast.makeText(GroupAvailableActivity.this,
                            String.valueOf(user_group_names.size()),
                            Toast.LENGTH_LONG).show();*/
                }
            }
        });

    }

    public void compareAndDisplayList(ArrayList<String> db_group_names, ArrayList<String> user_group_names){

        /*Toast.makeText(GroupAvailableActivity.this,
                "TEST",
                Toast.LENGTH_LONG).show();*/


        for (int dbIt = 0; dbIt < db_group_names.size(); dbIt++){
            boolean userIsPartOfGroup = false;
            /*Toast.makeText(GroupAvailableActivity.this,
                    "DB:" + db_group_names.get(dbIt),
                    Toast.LENGTH_LONG).show();*/
            for (int userIt = 0; userIt < user_group_names.size(); userIt++){
                /*Toast.makeText(GroupAvailableActivity.this,
                        "USER:" + user_group_names.get(userIt),
                        Toast.LENGTH_LONG).show();*/
                if (db_group_names.get(dbIt).equals(user_group_names.get(userIt))){
                    userIsPartOfGroup = true;
                }
                /*Toast.makeText(GroupAvailableActivity.this,
                        String.valueOf(userIsPartOfGroup),
                        Toast.LENGTH_LONG).show();*/
            }

            if (userIsPartOfGroup == false){
                availableGroupsList.add(db_group_names.get(dbIt));
            }
        }

        Collections.sort(availableGroupsList, String.CASE_INSENSITIVE_ORDER);
        adapter.notifyDataSetChanged();
    }
}
