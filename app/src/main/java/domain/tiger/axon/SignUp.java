package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    private Button submit ;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        submit = findViewById(R.id.doneButton);
        db = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput = (EditText) findViewById(R.id.emailInput);
                EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
                EditText addressInput = (EditText) findViewById(R.id.addressInput);

                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String address = addressInput.getText().toString();

                Map<String, Object> userInfo = new HashMap<>();

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

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUp.this, "Account creation succcessful!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUp.this, MainActivity.class));
                        } else {
                            Toast.makeText(SignUp.this, "Account creation unsuccessful!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /*userInfo.put("email", email);
                userInfo.put("password", password);
                userInfo.put("location", address);

                db.collection("users").add(userInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignUp.this, "Congrats! You created an account. Start by creating a group or joining one!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUp.this, group.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Sorry unable to create an account.", Toast.LENGTH_LONG).show();
                    }
                });*/

                //backHome();
            }
        });

    }


    /*public void backHome(){


        username = usernameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        address = addressInput.getText().toString();

       Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("username", username);
        dataToSave.put("email", email);
        userDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("firebase","doc has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("firebase", "doc was not saved");
            }
        });

        //Intent i = new Intent(this, MainActivity.class);
        //startActivity(i);
    }*/

}
