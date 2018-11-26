package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //ListView vars
    private ArrayList<String> events = new ArrayList<>();
    private ListView lvEvents;
    private ArrayAdapter<String> adapter;

    //Category vars
    private TextView categoryName;
    private String category;

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
                startActivity(new Intent(EventListActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(EventListActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_event_list);

        categoryName = (TextView) findViewById(R.id.tvCategoryName);

        lvEvents = (ListView) findViewById(R.id.lvEventList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        lvEvents.setAdapter(adapter);

        //Determine what category the user wants to view and call FillEventList passing in the category
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                category = documentSnapshot.get("mostRecentEventsList").toString();
                categoryName.setText(category);
                switch (category){
                    case "Arts":
                        events.clear();
                        FillEventList("arts");
                        break;
                    case "Food & Drink":
                        events.clear();
                        FillEventList("food-and-drink");
                        break;
                    case "Festivals & Fairs":
                        events.clear();
                        FillEventList("festivals-fairs");
                        break;
                    case "Sports & Active Life":
                        events.clear();
                        FillEventList("sports-active-life");
                        break;
                    case "Nightlife":
                        events.clear();
                        FillEventList("nightlife");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /*
    Function: Accesses the events of type category from Firebase database.
    The events in the database are stored based on their price.
    The cheapest is in level 1 and the most expensive is level 4.
    We need to access each level separately.
    We start with the cheapest and make our way to the most expensive.
    We store the events we find and we update the adapter to reflect that change to the user.

    Input:
    String category: the category the user wants to view
     */
    public void FillEventList(String category){
        //Price level 1
        db.collection(category).document("events").collection("1").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    events.add(qsList.get(i).get("name").toString());
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Price level 2
        db.collection(category).document("events").collection("2").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    events.add(qsList.get(i).get("name").toString());
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Price level 3
        db.collection(category).document("events").collection("3").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    events.add(qsList.get(i).get("name").toString());
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Price level 4
        db.collection(category).document("events").collection("4").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    events.add(qsList.get(i).get("name").toString());
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
