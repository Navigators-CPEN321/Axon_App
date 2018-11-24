package domain.tiger.axon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String emailAddress;
    private TextView emailTV;
    private Button buttonEamilChange;
    public boolean emailFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailTV = (TextView) findViewById(R.id.resetPasswordText);
        buttonEamilChange = (Button) findViewById(R.id.resetPasswordButton);

        buttonEamilChange.setOnClickListener(this);
    }


    public void onClick(View view) {

        if (view.equals(buttonEamilChange)) {
            changePasword();
        }
    }

    public void changePasword() {
        emailAddress = emailTV.getText().toString();

        if(validateEmail(emailAddress)){

            auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Email sent", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }

    }

    public boolean validateEmail(final String emailAddress) {

        if(emailAddress.isEmpty()){
            emailTV.setError("Email is required");
            emailTV.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            emailTV.setError("Invalid email");
            emailTV.requestFocus();
            return false;
        }

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                emailFound = false;

                for (int k = 0; k < qsList.size(); k++) {
                    if (qsList.get(k).get("email").equals(emailAddress)) {
                        emailFound = true;
                        break;
                    }

                }

                if (emailFound){
                    Toast.makeText(ResetPasswordActivity.this, "email found in db", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(ResetPasswordActivity.this, "email NOT FOUND in db", Toast.LENGTH_LONG).show();

                }
            }
        });

        return emailFound;
    }
}

