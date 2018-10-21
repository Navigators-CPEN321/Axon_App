package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Preference extends AppCompatActivity implements View.OnClickListener {

    public Button submitPreferenceButton;
    private EditText cost_max_input;
    private Spinner categoryInput;
    private int cost_max;
    private String category;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.75), (int)(height * 0.75));

        /*
        Get preferences
         */
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(Preference.this, MainActivity.class));
        }

        db = FirebaseFirestore.getInstance();

        cost_max_input = (EditText) findViewById(R.id.priceInput);
        categoryInput = (Spinner) findViewById(R.id.spinnerCategory);
        submitPreferenceButton = (Button) findViewById(R.id.submitPreference);

        submitPreferenceButton.setOnClickListener(this);

    }

    private void submitPreferences() {
        cost_max = Integer.parseInt(cost_max_input.getText().toString());
        category = categoryInput.getSelectedItem().toString();

        FirebaseUser user = auth.getCurrentUser();

        userInformation userInfo = new userInformation(cost_max, category, user.getUid());

        db.collection("users").add(userInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Preference.this, "Preferences saved", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Preference.this, group_view.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Preference.this, "Failed to save your preferences.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View view){
        if (view == submitPreferenceButton){
            submitPreferences();
        }
    }


}
