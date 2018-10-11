package domain.tiger.axon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private Button signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp = findViewById(R.id.signUpButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    public void signUp(){

        Intent i = new Intent(this, SignUp.class);
        startActivity(i);

    }

}