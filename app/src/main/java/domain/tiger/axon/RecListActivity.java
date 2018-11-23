package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*
    RecListActivity Page:
        Shows the recommended activities list
 */
public class RecListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private ListView lvRecList;
    private ArrayList<String> selEventsList = new ArrayList<>();
    private String currentGroup;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_activities_list);

        //Connect listview
        lvRecList = (ListView) findViewById(R.id.lvRecList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selEventsList);
        lvRecList.setAdapter(adapter);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentGroup = documentSnapshot.get("currentGroup").toString();
                db.collection("groups").document(currentGroup).collection("sel_events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                        if (qsList.size() != 0){
                            selEventsList.clear();
                            for (int i = 0; i < qsList.size(); i++){
                                selEventsList.add(qsList.get(i).get("name").toString());
                            }
                            Collections.sort(selEventsList, String.CASE_INSENSITIVE_ORDER);
                            adapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(RecListActivity.this,
                                    "No events found",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecListActivity.this,
                                "No events found",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


}
