package domain.tiger.axon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    //Constants
    private int displayNameLength = 15;
    private int minPasswordLength = 6;

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //EditTexts
    private EditText etDisplayName;
    private EditText etOldPassword;
    private EditText etNewPassword;

    //Buttons
    private Button btnChangePassword;
    private Button submitBtn;

    //TextViews
    private TextView mTextMessage;
    private TextView userIdTextview;

    //Strings
    private String newDisplayName;
    private String userID;

    //Bottom navigation bar
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
                    Intent i = new Intent(SettingsActivity.this, SettingsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

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
                startActivity(new Intent(SettingsActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_settings);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Setup widgets
        submitBtn = (Button) findViewById(R.id.btnDisplayNameChange);
        userIdTextview = (TextView) findViewById(R.id.userIdTV);
        btnChangePassword = (Button) findViewById(R.id.btnPasswordChangeSubmit);
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);

        //Display display name
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userID = documentSnapshot.get("displayName").toString();
                userIdTextview.setText(userID);
            }
        });

        submitBtn.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    public void onClick(View view){
        if (view.equals(submitBtn)){
            changeDisplayName();
        }
        if (view.equals(btnChangePassword)){
            changePassword();
        }
    }

    /*
    Changes display name
    Procedure:
        1. Obtains new desired display name
        2. Checks if display name is valid (empty and proper length in that order)
        3. Updates display name if valid
     */
    public void changeDisplayName(){
        etDisplayName = (EditText) findViewById(R.id.etDisplayName);

        newDisplayName = etDisplayName.getText().toString();

        if (newDisplayName.isEmpty()){
            etDisplayName.setError("Display name can't be empty");
            etDisplayName.requestFocus();
            return;
        }

        if (newDisplayName.length() > displayNameLength){
            etDisplayName.setError("Display name can only be " + String.valueOf(displayNameLength) + " characters long");
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

    /*
     User enters old password and a new password.
     Checks if both are valid (empty and long enough).
     If old password is correct update password to be new password.
     Procedure:
        1. Checks if old and new password are valid
        2. If valid, check if old password is correct
        3. If old password is correct, update password to be new password
     */
    public void changePassword(){
        if (etOldPassword.getText().toString().isEmpty()){
            etOldPassword.setError("Password is empty");
            etOldPassword.requestFocus();
            return;
        }

        if (etNewPassword.getText().toString().isEmpty()){
            etNewPassword.setError("Password is empty");
            etNewPassword.requestFocus();
            return;
        }

        if (etNewPassword.getText().toString().length() < minPasswordLength){
            etNewPassword.setError("Password is too short. Needs to be longer than 6 letters");
            etNewPassword.requestFocus();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), etOldPassword.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.updatePassword(etNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this,
                                        "Password change successful.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this,
                            "Password change unsuccessful.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
