package domain.tiger.axon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class group_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        goToPreferencesPage();
    }


    /*Login

     */
    public void goToPreferencesPage(){
        Button btnRecommendations= findViewById(R.id.btnRecommendations);

        btnRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(group_view.this, Preference.class));
            }
        });
    }
}
