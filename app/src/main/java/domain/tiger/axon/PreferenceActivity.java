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

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/*
Preference Page:
Allows the user to enter their personal preferences. The app then locates the preference associated with that group and that user and updates it.
 */
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
    private FirebaseAuth auth = FirebaseAuth.getInstance();;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();

    //Random
    private String currentGroup;
    private String prefRefStr;
    private String prefrefFirstPart;
    private String prefrefSecondPart;

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

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(PreferenceActivity.this, MainActivity.class));
        }

        cost_max_input = (EditText) findViewById(R.id.priceInput);
        categoryInput = (Spinner) findViewById(R.id.spinnerCategory);
        submitPreferenceButton = (Button) findViewById(R.id.submitPreference);

        submitPreferenceButton.setOnClickListener(this);

    }
    /*
    submitPreferences function:
        Takes user preferences and stores on FireBase database.
    Procedure:
        1. Get user preferences
        2. Store a new preference on FireBase database
     */
    private void submitPreferences() {

        //Get user Preferences

        String cost_max_string = cost_max_input.getText().toString();

        if (cost_max_string.isEmpty()){
            cost_max_input.setError("Please enter the most your willing to spend. Enter 0 if you're not willing to spend any money");
            cost_max_input.requestFocus();
            return;
        }

        cost_max = Integer.parseInt(cost_max_input.getText().toString());
        category = categoryInput.getSelectedItem().toString();

        //Get current group
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentGroup = documentSnapshot.get("currentGroup").toString();
                Toast.makeText( PreferenceActivity.this,
                        "Checkpoint 1 and current group is " + currentGroup,
                        Toast.LENGTH_LONG).show();
                db.collection("groups").document(currentGroup)
                        .collection("prefrefs").document(user.getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        prefRefStr = documentSnapshot.get("prefRef").toString();
                        prefrefFirstPart = prefRefStr.substring(0, prefRefStr.length() - 6);
                        prefrefSecondPart = prefRefStr.substring(prefRefStr.length() - 5, prefRefStr.length());
                        Preferences pref = new Preferences(cost_max, category, user.getUid());
                        Toast.makeText( PreferenceActivity.this,
                                "Checkpoint 2 and " + prefrefFirstPart + prefrefSecondPart,
                                Toast.LENGTH_LONG).show();
                        db.collection(prefrefFirstPart).document(prefrefSecondPart).set(pref).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( PreferenceActivity.this,
                                        "Preferences saved",
                                        Toast.LENGTH_LONG).show();
                                Map<String, String> httpMap = new HashMap<>();
                                httpMap.put("group", currentGroup);
                                functions.getHttpsCallable("selectEvents").call(httpMap);
                                functions.getHttpsCallable("writePrefs").call(httpMap);
                                functions.getHttpsCallable("findGroupEvents").call(httpMap);
                                startActivity(new Intent(PreferenceActivity.this, RecListActivity.class));
                            }
                        });
                    }
                });

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
