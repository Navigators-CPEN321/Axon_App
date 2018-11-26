package domain.tiger.axon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private double screenWidthFactor = 0.75;
    private double screenHeightFactor = 0.75;

    private EditText  etReport;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String reportMsg;
    private Button btnReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        popUpWindow();

        etReport = (EditText) findViewById(R.id.etReportMsg);
        btnReport = (Button) findViewById(R.id.btnReport);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMsg = etReport.getText().toString();

                if (reportMsg.length() < 250){
                    Map<String, String> reportMap = new HashMap<>();
                    reportMap.put("Report", reportMsg);
                    db.collection("reports").document().set(reportMap);
                    onBackPressed();
                } else {
                    etReport.setError("Please shorten your report");
                    etReport.requestFocus();
                }
            }
        });
    }

    public void popUpWindow(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*screenWidthFactor), (int)(height * screenHeightFactor));
    }
}
