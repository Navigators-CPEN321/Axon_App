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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase vars
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Input vars
    private String emailAddress;
    private Button buttonEmailChange;
    private TextView emailTV;
    public boolean emailFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailTV = (TextView) findViewById(R.id.resetPasswordText);
        buttonEmailChange = (Button) findViewById(R.id.resetPasswordButton);

        buttonEmailChange.setOnClickListener(this);
    }


    public void onClick(View view) {

        if (view.equals(buttonEmailChange)) {
            changePassword();
        }
    }

    public void changePassword() {
        emailAddress = emailTV.getText().toString();

        validateAndSendEmail(emailAddress);

    }

    /*
    Check if email is empty and valid email. Sends email
     */
    public void validateAndSendEmail(final String emailAddress) {

        if(emailAddress.isEmpty()){
            emailTV.setError("Email is required");
            emailTV.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            emailTV.setError("Invalid email");
            emailTV.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Email sent",
                            Toast.LENGTH_LONG).show();

                } else{
                    Toast.makeText(ResetPasswordActivity.this,
                            "Was unable to send email.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

















//Get all the Axon emails and check if the email entered is registered with Axon
        /*db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                if (emailFound) {
                    auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this,
                                        "Email sent",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetPasswordActivity.this,
                            "The email you entered is not registered with Axon",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/

