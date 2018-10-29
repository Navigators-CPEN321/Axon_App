package domain.tiger.axon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

public class GroupNavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;

    private Button btnGroupView, btnGroupCreate, btnLogOut;

    //Navigation bar
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //Navigation bar (unused at the moment)
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Check if a user is logged in. If not redirect them to login page
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
        }

        btnGroupCreate = (Button) findViewById(R.id.btnGroupCreate);
        btnGroupView = (Button) findViewById(R.id.btnGroupView);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        btnGroupCreate.setOnClickListener(this);
        btnGroupView.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
    }

    /*
    App navigation:
        Redirects the user to the GroupCreateActivity, GroupViewActivity, or the login page
     */
    public void onClick(View view){
        if (view.equals(btnGroupCreate)){
            startActivity(new Intent(GroupNavigationActivity.this, GroupCreateActivity.class));
        }
        if (view.equals(btnGroupView)){
            startActivity(new Intent(GroupNavigationActivity.this, GroupViewActivity.class));
        }
        if (view.equals(btnLogOut)){
            auth.signOut();
            startActivity(new Intent(GroupNavigationActivity.this, MainActivity.class));
        }
    }
}
