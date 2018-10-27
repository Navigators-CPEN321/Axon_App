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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //Buttons, EditText, Spinners
    private Button signUp ;
    private EditText emailInput, passwordInput, addressInput;
    private Spinner dobMonthInput, dobDayInput, dobYearInput;

    //Auth variables
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String email, address, dobMonth, dobDay, dobYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        signUp = (Button) findViewById(R.id.doneButton);
        db = FirebaseFirestore.getInstance();


        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        addressInput = (EditText) findViewById(R.id.addressInput);
        dobMonthInput = (Spinner) findViewById(R.id.spinnerMonth);
        dobDayInput = (Spinner) findViewById(R.id.spinnerDay);
        dobYearInput = (Spinner) findViewById(R.id.spinnerYear);

        signUp.setOnClickListener(this);

    }


    /*
    Login function:
        User enters their personal information to create an account
    Procedure:
        1. Get the email, password, date of birth, and address
        2. Check if the information entered is acceptable
        3. Use FireBase authentication to create an account by entering email and password (date of birth and address not needed)
            Success:
                    a. Create a user document on FireBase for user later
                    b. Go to the login page
             Failure:
                    a. Display failure message
     */
    private void signUp(){

        String password;

        //Get the email, password, date of birth, and address
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        address = addressInput.getText().toString();
        dobMonth = dobMonthInput.getSelectedItem().toString();
        dobDay = dobDayInput.getSelectedItem().toString();
        dobYear = dobYearInput.getSelectedItem().toString();

        //Check if the information entered is acceptable
        if (email.isEmpty()){
            emailInput.setError("Email is required.");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Invalid email.");
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

        if (address.isEmpty()){
            addressInput.setError("Address is required.");
            addressInput.requestFocus();
            return;
        }

        //Use Firebase authentication to create an account by entering email and password (date of birth not needed)
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();

                    //Display success message
                    Toast.makeText(SignUpActivity.this, "Account creation succcessful!", Toast.LENGTH_LONG).show();

                    //Create a user document on FireBase for user later
                    UserInformation userInfo = new UserInformation(email, address, dobMonth, dobDay, dobYear, user.getUid());
                    db.collection("users").document(email).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUpActivity.this, "Account creation successful!", Toast.LENGTH_LONG).show();
                        }
                    });

                    //Go to the login page
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                } else {
                    //Display failure message
                    Toast.makeText(SignUpActivity.this, "Account creation unsuccessful!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /*
    App navigation:
        Redirects the user to the login page
     */
    public void onClick(View view){
        if (view.equals(signUp)){
            signUp();
        }
    }

}
