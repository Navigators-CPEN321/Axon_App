package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/*
The GroupCreateActivity provides the functionality to the group create page.
It allows users to enter a group name, which then creates a group on FireBase.
 */
public class GroupCreateActivity extends AppCompatActivity{

    private final double screenWidthFactor = 0.75;
    private final double screenHeightFactor = 0.75;

    //Firebase variables
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);


        //Pop-up window display

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*screenWidthFactor), (int)(height * screenHeightFactor));



        //Checks if user is logged in. If not redirects to login page

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(GroupCreateActivity.this, MainActivity.class));
        }



        //Creates a group

        groupCreate();
    }

    /*
        groupCreate function:
            Creates a group with the name the user entered
        Procedure:
            1. Checks the group name entered to see if it is empty
            2. Creates the group storing on the FireBase database and redirects user to the GroupViewActivity page to see their group

     */
    private void groupCreate() {
        db = FirebaseFirestore.getInstance();

        Button groupCreateSubmit = (Button) findViewById(R.id.btnGroupCreateSubmit);

        groupCreateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = auth.getCurrentUser();

                //Checks the group name entered

                EditText groupNameInput= (EditText) findViewById(R.id.etGroupName);
                final String groupName = groupNameInput.getText().toString();
                if (groupName.isEmpty()) {
                    groupNameInput.setError("No group name was entered.");
                    groupNameInput.requestFocus();
                    return;
                }


                //Creates the group storing on the FireBase database and redirects user to the GroupViewActivity page to see their group
                db.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            //Create group and store on Firebase
                            Group newGroup = new Group(groupName);
                            db.collection("groups").document(groupName).set(newGroup).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText( GroupCreateActivity.this,
                                                    "Congrats! You created a group. Now invite some friends!",
                                                    Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText( GroupCreateActivity.this,
                                                    "Group creation failed.",
                                                    Toast.LENGTH_LONG).show();

                                }
                            });

                            //Add the creator of the group to the group
                            newGroup.addCreator(user.getUid());
                            startActivity(new Intent(GroupCreateActivity.this, GroupViewActivity.class));

                        }
                    }
                });


            }
        });
    }

}
