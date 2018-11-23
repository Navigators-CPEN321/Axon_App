package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/*
Sign-up page:
Lets user enter email, password, date of birth, and address to create an account.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //Constant
    private final int passwordLength = 6;

    //Buttons, EditText, Spinners
    private Button signUp ;
    private EditText emailInput, passwordInput, addressInput, displayNameInput;
    private Spinner dobMonthInput, dobDayInput, dobYearInput;

    //Auth variables
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String email, displayName, password, dobDay, dobMonth, dobYear, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Instantiating FireBase variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Connecting Buttons, EditTexts, and Spinners
        signUp = (Button) findViewById(R.id.doneButton);
        emailInput = (EditText) findViewById(R.id.emailInput);
        displayNameInput = (EditText) findViewById(R.id.etDisplayName);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        dobMonthInput = (Spinner) findViewById(R.id.spinnerMonth);
        dobDayInput = (Spinner) findViewById(R.id.spinnerDay);
        dobYearInput = (Spinner) findViewById(R.id.spinnerYear);
        addressInput = (EditText) findViewById(R.id.addressInput);

        //Setting up button
        signUp.setOnClickListener(this);
    }


    /*
    Login function:
        User enters their personal information to create an account
    Procedure:
        1. Get the email, password, date of birth, and address from user
        2. Check if the information entered is acceptable
        3. Use FireBase authentication to create an account by entering email and password (date of birth and address not needed)
            Success:
                    a. Create a user document on FireBase for user later
                    b. Go to the login page
             Failure:
                    a. Display failure message
     */
    private void signUp(){

        //Get the email, password, date of birth, and address from user
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        displayName = displayNameInput.getText().toString();
        dobMonth = dobMonthInput.getSelectedItem().toString();
        dobDay = dobDayInput.getSelectedItem().toString();
        dobYear = dobYearInput.getSelectedItem().toString();
        address = addressInput.getText().toString();


        if (!validateAccountInfo(email, password, displayName, address)){
            return;
        }

        //Use Firebase authentication to create an account by entering email and password (date of birth and address not needed)
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser user = mAuth.getCurrentUser();

                    //Store a copy of the user information on FireBase database excluding password
                    UserInformation userInfo = new UserInformation(email, displayName, dobMonth, dobDay, dobYear, address, user.getUid());

                    db.collection("users").document(user.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText( SignUpActivity.this,
                                            "Account creation successful!",
                                             Toast.LENGTH_LONG).show();

                        }

                    });
                    user.sendEmailVerification();
                    Toast.makeText( SignUpActivity.this,
                            "Verification email sent.",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                } else {

                    Toast.makeText( SignUpActivity.this,
                                    "Account creation unsuccessful.",
                                    Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    /*
    App navigation:
        Redirects the user to the login page once sign up is complete
     */
    public void onClick(View view){
        if (view.equals(signUp)){
            signUp();
        }
    }

    /*
    Validate email, password, display name, and address
     */
    public boolean validateAccountInfo(String email, String password, String displayName, String address){
        if (email.isEmpty()){
            emailInput.setError("Email is required.");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Invalid email.");
            emailInput.requestFocus();
            return false;
        }

        if (displayName.isEmpty()){
            displayNameInput.setError("Display name is required.");
            displayNameInput.requestFocus();
            return false;
        }

        if (password.isEmpty()){
            passwordInput.setError("Password is required.");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length()< passwordLength){
            passwordInput.setError("Password needs to be longer than 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        if (address.isEmpty()){
            addressInput.setError("Address is required.");
            addressInput.requestFocus();
            return false;
        }

        return true;
    }

}
