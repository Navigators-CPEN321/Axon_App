package domain.tiger.axon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInviteActivity extends AppCompatActivity implements View.OnClickListener{

    //Screen display constants
    private final double screenWidthFactor = 0.85;
    private final double screenHeightFactor = 0.50;

    //Firebase vars;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //Activity widgets
    private EditText etInviteFriend;
    private Button btnSubmit;
    private boolean emailFound;
    private String currentGroup;
    private String email;
    private String friendUSID;
    private int invitationSize;
    private boolean full;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        popUpWindow();

        etInviteFriend = (EditText) findViewById(R.id.etInviteFriend);
        btnSubmit = (Button) findViewById(R.id.btnInviteFriend);

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentGroup = documentSnapshot.get("currentGroup").toString();
            }
        });

        btnSubmit.setOnClickListener(this);

    }

    public void onClick(View view) {

        if (view.equals(btnSubmit)) {
            //sendInvite();
            email = etInviteFriend.getText().toString();
            inviteFriend(email);
        }
    }

    /*
    Invites friend to the group
    Procedure:
    1. Check if email is valid
        -empty
        -valid email
    2. Check if the group is full or not
        -Full: Don't send invite
        -Not full: Go step 3
    3. Check if user is registered with Axon
        -Registered: send email
        -Not registered: don't send
     */
    public boolean inviteFriend(final String email){
        if(email.isEmpty()){
            etInviteFriend.setError("Email is required");
            etInviteFriend.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etInviteFriend.setError("Invalid email");
            etInviteFriend.requestFocus();
            return false;
        }

        //Check if group is full
        db.collection("groups").document(currentGroup).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                full = false;
                if (Integer.valueOf(documentSnapshot.get("size").toString()) >= 8){
                    full = true;
                }
            }
        });


        //Check if user is in database (verified registered account)
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!full) {
                    List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                    emailFound = false;

                    for (int k = 0; k < qsList.size(); k++) {
                        if (qsList.get(k).get("email").equals(email)) {
                            friendUSID = qsList.get(k).get("usid").toString();
                            emailFound = true;
                            invitationSize = Integer.valueOf(qsList.get(k).get("invitations").toString());
                            break;
                        }

                    }

                    //Send email if registered. If not tell them the account is not a registered account.
                    if (emailFound) {
                        sendInvite();
                    } else {
                        Toast.makeText(GroupInviteActivity.this,
                                "Sorry that account isn't registered with Axon!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(GroupInviteActivity.this,
                            "Sorry your group is full! You can't invite anymore people.",
                            Toast.LENGTH_LONG).show();

                }
            }
        });
        return emailFound;
    }

    /*
    Sends invite
    Invitations is stored in the users information
     */
    public void sendInvite(){
        db.collection("groups/" + currentGroup+ "/users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < qsList.size(); i++){
                    if(qsList.get(i).get("email").toString().equalsIgnoreCase(email)){
                        Toast.makeText(GroupInviteActivity.this,
                                "User is already part of the group",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GroupInviteActivity.this,
                                "Invitation sent!",
                                Toast.LENGTH_LONG).show();

                        //Store invitation
                        Map<String, Object> invMap = new HashMap<>();
                        invMap.put("group_name", (String) currentGroup);
                        db.collection("users").document(friendUSID)
                                .collection("invitations").document(currentGroup).set(invMap);
                        db.collection("users").document(friendUSID)
                                .update("invitations", String.valueOf(invitationSize + 1));
                    }
                }
            }
        });
    }

    /*
    Display as pop up window
     */
    public void popUpWindow(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*screenWidthFactor), (int)(height * screenHeightFactor));
    }
}
