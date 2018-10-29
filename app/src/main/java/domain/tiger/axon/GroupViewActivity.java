package domain.tiger.axon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
Group View Page:
    Allows user to view information about the group they are in.
    Also, there are two functionalities on the page:
        1. Allows the user to go to PreferenceActivity page to update their preferences.
        2. Allows the user to go to RecListActivity page to view the recommended activities we generated for them.
 */
public class GroupViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        goToPreferencesPage();
        goToRecActivitiesList();
    }



    public void goToPreferencesPage(){
        Button btnRecommendations= findViewById(R.id.btnRecommendations);

        btnRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupViewActivity.this, PreferenceActivity.class));
            }
        });
    }


    public void goToRecActivitiesList(){
        Button btnActivities = findViewById(R.id.btnActivities);

        btnActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupViewActivity.this, RecListActivity.class));
            }
        });
    }
}
