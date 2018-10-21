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
import com.google.firebase.auth.FirebaseUser;

public class group extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;

    private FirebaseAuth auth;

    private Button btnGroupView, btnGroupCreate, btnLogOut;

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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(group.this, MainActivity.class));
        }

        btnGroupCreate = (Button) findViewById(R.id.btnGroupCreate);
        btnGroupView = (Button) findViewById(R.id.btnGroupView);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        btnGroupCreate.setOnClickListener(this);
        btnGroupView.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
    }


    public void onClick(View view){
        if (view == btnGroupCreate){
            startActivity(new Intent(group.this, group_create.class));
        }
        if (view == btnGroupView){
            startActivity(new Intent(group.this, group_view.class));
        }
        if (view == btnLogOut){
            auth.signOut();
            startActivity(new Intent(group.this, MainActivity.class));
        }
    }
}
