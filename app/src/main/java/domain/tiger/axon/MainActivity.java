package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText emailInput, passwordInput;
    private Button signUp, btnLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //If user is already logged in, then go to group page
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this, group.class));
        }

        //Connecting the EditTexts and Buttons
        emailInput = (EditText) findViewById(R.id.editTextEmail);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        signUp = (Button) findViewById(R.id.signUpButton);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        signUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    /*
    Login function:
        User enters their login information and the app validates their credentials
    Procedure:
        1. Get the email and password the user enters
        2. Check if the login information that entered is acceptable
        3. Use FireBase authentication to validate their login information
     */
    private void login(){

        //Get the email and password the user enters
        String email, password;
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        //Check if the login information that entered is acceptable
        if (email.isEmpty()){
            emailInput.setError("Email is required.");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()){
            passwordInput.setError("Password is required.");
            passwordInput.requestFocus();
            return;
        }

        if (password.length()<6){
            passwordInput.setError("Password needs to be longer than 6 characters");
            passwordInput.requestFocus();
            return;
        }

        //Use FireBase authentication to validate their login information
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    Toast.makeText(MainActivity.this, "Log in successful.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, group.class));
                } else{
                    Toast.makeText(MainActivity.this, "Log in failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
    App navigation:
        Redirects the user to the group page if login is successful or the sign up page
     */
    public void onClick(View view){
        if (view.equals(btnLogin)){
            login();
        }
        if (view.equals(signUp)){
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }
    }

}