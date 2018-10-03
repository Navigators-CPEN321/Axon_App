package domain.tiger.axon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {

    private Button submit ;

    String username, email, password, address;
    EditText usernameInput, emailInput, passwordInput, addressInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        submit = findViewById(R.id.doneButton);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        addressInput = findViewById(R.id.addressInput);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = usernameInput.getText().toString();
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                address = addressInput.getText().toString();

                //backHome();
            }
        });

    }


    public void backHome(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
