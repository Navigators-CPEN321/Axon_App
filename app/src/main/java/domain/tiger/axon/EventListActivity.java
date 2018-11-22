package domain.tiger.axon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class EventListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String category;
    private TextView categoryName;
    private ArrayList<String> events = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lvEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        categoryName = (TextView) findViewById(R.id.tvCategoryName);

        lvEvents = (ListView) findViewById(R.id.lvEventList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        lvEvents.setAdapter(adapter);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                category = documentSnapshot.get("mostRecentEventsList").toString();
                Toast.makeText(EventListActivity.this, category, Toast.LENGTH_LONG).show();
                categoryName.setText(category);
                switch (category){
                    case "Music":
                        FillEventList("music");
                        break;
                    case "Visual Arts ":
                        FillEventList("visual-arts");
                        break;
                    case "Performing Arts":
                        FillEventList("performing-arts");
                        break;
                    case "Film":
                        FillEventList("film");
                        break;
                    case "Lectures & Books":
                        FillEventList("lectures-books");
                        break;
                    case "Fashion":
                        FillEventList("fashion");
                        break;
                    case "Food & Drink":
                        FillEventList("food-and-drink");
                        break;
                    case "Festivals & Fairs":
                        FillEventList("festivals-fairs");
                        break;
                    case "Charities":
                        FillEventList("charities");
                        break;
                    case "Sports & Active Life":
                        FillEventList("sports-active-life");
                        break;
                    case "Nightlife":
                        FillEventList("nightlife");
                        break;
                    case "Kids & Family":
                        FillEventList("kids-family");
                        break;
                    case "Other":
                        FillEventList("other");
                        break;
                    default:
                        break;
                }
            }
        });

        /*switch (category){
            case "Music":
                FillEventList("music");
                break;
            case "Visual Arts ":
                FillEventList("visual-arts");
                break;
            case "Performing Arts":
                FillEventList("performing-arts");
                break;
            case "Film":
                FillEventList("film");
                break;
            case "Lectures & Books":
                FillEventList("lectures-books");
                break;
            case "Fashion":
                FillEventList("fashion");
                break;
            case "Food & Drink":
                FillEventList("food-and-drink");
                break;
            case "Festivals & Fairs":
                FillEventList("festivals-fairs");
                break;
            case "Charities":
                FillEventList("charities");
                break;
            case "Sports & Active Life":
                FillEventList("sports-active-life");
                break;
            case "Nightlife":
                FillEventList("nightlife");
                break;
            case "Kids & Family":
                FillEventList("kids-family");
                break;
            case "Other":
                FillEventList("other");
                break;
            default:
                break;
        }*/
    }

    public void FillEventList(String category){
        Toast.makeText(EventListActivity.this,
                "Checkpoint1",
                Toast.LENGTH_LONG).show();
        db.collection(category).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                events.clear();
                for (int i = 0; i < qsList.size(); i++){
                    events.add(qsList.get(i).get("name").toString());
                }
                Collections.sort(events, String.CASE_INSENSITIVE_ORDER);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
