package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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



    String username, email, password, address;
    EditText usernameInput, emailInput, passwordInput, addressInput;


    //setting up firebase reference
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    private DocumentReference userDocRef = FirebaseFirestore.getInstance().document("users/user4");
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



                backHome();
            }
        });

    }


    public void backHome(){


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
    }

}
