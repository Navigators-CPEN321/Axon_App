package domain.tiger.axon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTextMessage;
    private TextView userIdTextview;
    private EditText etDisplayName;
    private Button submitBtn;
    private String newDisplayName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String userID;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(SettingsActivity.this, GroupNavigationActivity.class));
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(SettingsActivity.this, EventsCatalogActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        submitBtn = (Button) findViewById(R.id.btnDisplayNameChange);
        userIdTextview = (TextView) findViewById(R.id.userIdTV);



        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userID = documentSnapshot.get("displayName").toString();
                userIdTextview.setText(userID);
            }
        });

        submitBtn.setOnClickListener(this);
    }

    public void onClick(View view){
        if (view.equals(submitBtn)){
            changeDisplayName();
        }
    }

    public void changeDisplayName(){
        etDisplayName = (EditText) findViewById(R.id.etDisplayName);

        newDisplayName = etDisplayName.getText().toString();
        /*Toast.makeText(SettingsActivity.this,
                "newDisplayName is " + newDisplayName,
                Toast.LENGTH_LONG).show();*/

        if (newDisplayName.isEmpty()){
            etDisplayName.setError("Display name can't be empty");
            etDisplayName.requestFocus();
            return;
        }

        db.collection("users").document(user.getUid()).update("displayName", newDisplayName);
        finish();
        startActivity(getIntent());
        Toast.makeText(SettingsActivity.this,
                "Display name updated!",
                Toast.LENGTH_LONG).show();
    }

}
