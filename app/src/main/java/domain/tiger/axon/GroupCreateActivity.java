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

public class GroupCreateActivity extends AppCompatActivity{

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        /*
        Pop-up window display
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.75), (int)(height * 0.75));

        /*
        Checks if user is logged in. If not redirects to login page
         */
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(GroupCreateActivity.this, MainActivity.class));
        }

        //Creates a GroupNavigationActivity
        groupCreate();
    }

    /*
        groupCreate function:
            Creates a GroupNavigationActivity with the name the user entered
        Procedure:
            1. Checks the GroupNavigationActivity name entered
            2. Creates the GroupNavigationActivity storing on the FireBase database
            3. Go to personal GroupNavigationActivity page
     */
    private void groupCreate() {
        db = FirebaseFirestore.getInstance();

        Button groupCreateSubmit = (Button) findViewById(R.id.btnGroupCreateSubmit);

        groupCreateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = auth.getCurrentUser();

                //Checks the GroupNavigationActivity name entered
                EditText groupNameInput= (EditText) findViewById(R.id.etGroupName);
                final String groupName = groupNameInput.getText().toString();
                if (groupName.isEmpty()) {
                    return;
                }

                db.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            //Just a way to store the GroupNavigationActivity name on FireBase
                            int count = 1;
                            for(DocumentSnapshot document : task.getResult()){
                                count++;
                            }

                            ////Creates the GroupNavigationActivity storing on the FireBase database
                            Map<String, String> groupNameMap = new HashMap<>();
                            groupNameMap.put("group_name", groupName);
                            groupNameMap.put("usid", user.getUid());

                            db.collection("groups").document("GroupNavigationActivity" + count).set(groupNameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //Go to personal GroupNavigationActivity page
                                    Toast.makeText(GroupCreateActivity.this, "Congrats! You created a GroupNavigationActivity. Now invite some friends!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(GroupCreateActivity.this, GroupViewActivity.class));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GroupCreateActivity.this, "Group creation failed.", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                });


            }
        });
    }

}
