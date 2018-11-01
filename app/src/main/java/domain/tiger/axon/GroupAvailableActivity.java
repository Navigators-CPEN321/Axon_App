package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;

public class GroupAvailableActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listViewAvailableGroups;
    private ArrayList<String> availableGroupsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_available);

        listViewAvailableGroups = (ListView) findViewById(R.id.listAvailableGroups);

        final AvailableGroupAdapter adapter = new AvailableGroupAdapter(availableGroupsList,this);
        listViewAvailableGroups.setAdapter(adapter);

        db.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(DocumentSnapshot doc : task.getResult()){
                        String group_name = doc.get("group_name").toString();
                        availableGroupsList.add(group_name);
                        Collections.sort(availableGroupsList, String.CASE_INSENSITIVE_ORDER);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }
}
