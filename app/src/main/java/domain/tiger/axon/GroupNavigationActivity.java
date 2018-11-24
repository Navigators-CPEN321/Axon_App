package domain.tiger.axon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
Group Function Navigation Page:
Allows users to navigate to the different group pages to use that group functionality
 */
public class GroupNavigationActivity extends AppCompatActivity implements View.OnClickListener {





    private Button btnGroupView, btnGroupCreate, btnLogOut, btnGroupAvailable;
    private TextView usernameTextView;
    private String userDisplayName;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.invitations:
                startActivity(new Intent(GroupNavigationActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
                break;
        }
        return true;
    }

    //Navigation bar
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(GroupNavigationActivity.this, EventsCatalogActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(GroupNavigationActivity.this, SettingsActivity.class));
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //Navigation bar
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Check if a user is logged in. If not redirect them to login page
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
        }

        //Connecting buttons
        btnGroupCreate = (Button) findViewById(R.id.btnGroupCreate);
        btnGroupView = (Button) findViewById(R.id.btnGroupView);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnGroupAvailable = (Button) findViewById(R.id.btnGroupJoin);
        usernameTextView = (TextView) findViewById(R.id.usernameTV);

        //Setting up buttons
        btnGroupCreate.setOnClickListener(this);
        btnGroupAvailable.setOnClickListener(this);
        btnGroupView.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);

        //set username in the text box
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               userDisplayName = documentSnapshot.get("displayName").toString();
                System.out.println("debug: " + userDisplayName);
                usernameTextView.setText(userDisplayName);

            }
        });



    }

    /*
    App navigation:
        Redirects the user to the GroupCreateActivity, GroupAvailableActivity, GroupViewActivity, or the login page
     */
    public void onClick(View view){
        if (view.equals(btnGroupCreate)){
            //
            startActivity(new Intent(GroupNavigationActivity.this, GroupCreateActivity.class));
        }
        if(view.equals(btnGroupAvailable)){
            startActivity(new Intent(GroupNavigationActivity.this, GroupAvailableActivity.class));
        }
        if (view.equals(btnGroupView)){
            startActivity(new Intent(GroupNavigationActivity.this, UsersGroupsActivity.class));
        }
        if (view.equals(btnLogOut)){
            //auth.signOut();
            //startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
        }
    }
}
