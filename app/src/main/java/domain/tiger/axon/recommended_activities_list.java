package domain.tiger.axon;

import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class recommended_activities_list extends AppCompatActivity {

    private TextView event0text, event1text, event2text, event3text, event4text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_activities_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        event0text = (TextView) findViewById(R.id.event1);
        event1text = (TextView) findViewById(R.id.event2);
        event2text = (TextView) findViewById(R.id.event3);
        event3text = (TextView) findViewById(R.id.event4);
        event4text = (TextView) findViewById(R.id.event5);


        //create list
        for (int i = 0; i < 5; i++){
            createList(i);
        }

        /*ref = db.collection("groups/group1/sel_events").document("event1");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    event1text.setText(doc.get("name").toString());
                }
            }
        });

        ref = db.collection("groups/group1/sel_events").document("event2");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    event2text.setText(doc.get("name").toString());
                }
            }
        });
        ref = db.collection("groups/group1/sel_events").document("event3");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    event3text.setText(doc.get("name").toString());
                }
            }
        });
        ref = db.collection("groups/group1/sel_events").document("event4");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    event4text.setText(doc.get("name").toString());
                }
            }
        });
        ref = db.collection("groups/group1/sel_events").document("event5");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    event5text.setText(doc.get("name").toString());
                }
            }
        });*/

    }

    /*
    Creates the list by taking eventnum and finding the associated recommended event.
     */
    private void createList(final int eventnum){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        ref = db.collection("groups/group1/sel_events").document("event" + eventnum);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    switch (eventnum){
                        case 0: event0text.setText(doc.get("name").toString());
                            break;
                        case 1: event1text.setText(doc.get("name").toString());
                            break;
                        case 2: event2text.setText(doc.get("name").toString());
                            break;
                        case 3: event3text.setText(doc.get("name").toString());
                            break;
                        case 4: event4text.setText(doc.get("name").toString());
                            break;
                    }
                    }
                }
            });
    }
}
