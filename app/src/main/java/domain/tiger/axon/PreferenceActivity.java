package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener {

    //Screen display constants
    private final double screenWidthFactor = 0.75;
    private final double screenHeightFactor = 0.75;

    //Page Widgets
    public Button submitPreferenceButton;
    private EditText cost_max_input;
    private Spinner categoryInput;
    private int cost_max;
    private String category;

    //FireBase Variables
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);


        /*
        Display
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*screenWidthFactor), (int)(height * screenHeightFactor));

        /*
        Get Preferences
         */
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(PreferenceActivity.this, MainActivity.class));
        }

        db = FirebaseFirestore.getInstance();

        cost_max_input = (EditText) findViewById(R.id.priceInput);
        categoryInput = (Spinner) findViewById(R.id.spinnerCategory);
        submitPreferenceButton = (Button) findViewById(R.id.submitPreference);

        submitPreferenceButton.setOnClickListener(this);

    }
    /*
    submitPreferences function:
        Takes user Preferences and stores on FireBase database.
    Procedure:
        1. Get user Preferences
        2. Store on database
     */
    private void submitPreferences() {
        //Get user Preferences
        cost_max = Integer.parseInt(cost_max_input.getText().toString());
        category = categoryInput.getSelectedItem().toString();

        db.collection("groups/group1/prefs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    DocumentReference ref = db.collection("groups").document("group1");
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            int size;
                            if (task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();
                                size = Integer.parseInt(doc.get("size").toString());
                                FirebaseUser user = auth.getCurrentUser();
                                Preferences pref = new Preferences(cost_max, category, user.getUid());
                                db.collection("groups/group1/prefs").document("pref" + size).set(pref).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PreferenceActivity.this, "Preferences saved", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(PreferenceActivity.this, GroupViewActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PreferenceActivity.this, "Failed to save your Preferences.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                    //Store on database
                    //FirebaseUser user = auth.getCurrentUser();
                    //Preferences pref = new Preferences(cost_max, category, user.getUid());
                    /*db.collection("groups/group1/prefs").document("pref" + count).set(pref).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PreferenceActivity.this, "Preferences saved", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PreferenceActivity.this, GroupViewActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PreferenceActivity.this, "Failed to save your Preferences.", Toast.LENGTH_LONG).show();
                        }
                    });*/
                } else{
                    Toast.makeText(PreferenceActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    /*
    Submits Preferences
     */
    public void onClick(View view){
        if (view.equals(submitPreferenceButton)){
            submitPreferences();
        }
    }


}
