package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/*
    RecListActivity Page:
        Shows the recommended activities list
 */
public class RecListActivity extends AppCompatActivity {

    private TextView event0text, event1text, event2text, event3text, event4text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_activities_list);



        //Connect TextViews

        event0text = (TextView) findViewById(R.id.event1);
        event1text = (TextView) findViewById(R.id.event2);
        event2text = (TextView) findViewById(R.id.event3);
        event3text = (TextView) findViewById(R.id.event4);
        event4text = (TextView) findViewById(R.id.event5);


        for (int i = 0; i < 5; i++){
            updateRecActivitiesList(i);
        }

    }

    /*
    Creates the list by taking eventnum and finding the associated recommended event.
     */
    private void updateRecActivitiesList(final int eventnum){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        ref = db.collection("events").document("event" + eventnum);
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
