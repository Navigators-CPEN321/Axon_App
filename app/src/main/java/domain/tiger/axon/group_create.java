package domain.tiger.axon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

public class group_create extends AppCompatActivity {

    private Button groupCreateSubmit;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.75), (int)(height * 0.75));

        /*
        Creates a group using a name the user enters
            -if successful -> go to that group's page
            -if not successful -> notify the user
         */
        groupCreateSubmit = findViewById(R.id.btnGroupCreateSubmit);

        db = FirebaseFirestore.getInstance();

        groupCreateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText groupNameView = (EditText) findViewById(R.id.groupName);
                String groupName = groupNameView.getText().toString();
                if (groupName.isEmpty()) {
                    return;
                }

                Map<String, String> groupNameMap = new HashMap<>();
                groupNameMap.put("group_name", groupName);

                db.collection("groups").add(groupNameMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(group_create.this, "Congrats! You created a group. Now invite some friends!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(group_create.this, group_view.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(group_create.this, "Group creation failed.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
