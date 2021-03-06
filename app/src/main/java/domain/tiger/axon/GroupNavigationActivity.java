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

    //Widgets
    private Button btnGroupView, btnGroupCreate, btnGroupAvailable;
    private TextView usernameTextView;
    private String userDisplayName;

    //Firebase vars
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

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
                startActivity(new Intent(GroupNavigationActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(GroupNavigationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                auth.signOut();
                startActivity(intent);
                break;
            default:
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
                    Intent i = new Intent(GroupNavigationActivity.this, GroupNavigationActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
        }

        //Connecting buttons
        btnGroupCreate = (Button) findViewById(R.id.btnGroupCreate);
        btnGroupView = (Button) findViewById(R.id.btnGroupView);
        btnGroupAvailable = (Button) findViewById(R.id.btnGroupJoin);
        usernameTextView = (TextView) findViewById(R.id.usernameTV);

        //Setting up buttons
        btnGroupCreate.setOnClickListener(this);
        btnGroupAvailable.setOnClickListener(this);
        btnGroupView.setOnClickListener(this);

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
        Redirects the user to the GroupCreateActivity, GroupAvailableActivity or UserGroupsActivity
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
    }
}
