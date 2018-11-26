package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/*
Login page:
Allows the user to log in using their login information, or takes the user to the SignUpActivity page if the user wants to create a new account.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int passwordLength = 6;
    private EditText emailInput, passwordInput;
    private Button signUp, btnLogin;
    private TextView resetPassword;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If user is already logged in, then go to GroupNavigationActivity page
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this, GroupNavigationActivity.class));
        }

        //Connecting the EditTexts and Buttons
        emailInput = (EditText) findViewById(R.id.editTextEmail);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        signUp = (Button) findViewById(R.id.signUpButton);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        resetPassword = (TextView) findViewById(R.id.tvResetPasswordMain);

        //Setting up buttons
        signUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
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

        if (password.length()< passwordLength){
            passwordInput.setError("Password needs to be longer than 6 characters");
            passwordInput.requestFocus();
            return;
        }

        //Use FireBase authentication to validate their login information
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();

                    if (user.isEmailVerified()){
                        /*Toast.makeText( MainActivity.this,
                                "Log in successful.",
                                Toast.LENGTH_LONG).show();*/
                        startActivity(new Intent(MainActivity.this, GroupNavigationActivity.class));
                        finish();
                    } else {
                        auth.signOut();
                        Toast.makeText( MainActivity.this,
                                "Please verify your email.",
                                Toast.LENGTH_LONG).show();
                    }

                } else{

                    Toast.makeText( MainActivity.this,
                                    "Log in failed.",
                                    Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
    App navigation:
        Redirects the user to the GroupNavigationActivity page if login is successful or the SignUpActivity page for sign up
     */
    public void onClick(View view){
        if (view.equals(btnLogin)){
            login();
        }
        if (view.equals(signUp)){
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        }
        if (view.equals(resetPassword)){
            startActivity(new Intent(MainActivity.this, ResetPasswordActivity.class));
        }
    }

    public void onBackPressed() {
        //do nothing
    }

}