package domain.tiger.axon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*
Preference Page:
Allows the user to enter their personal preferences. The app then locates the preference associated with that group and that user and updates it.
 */
public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener  {

    //Screen display constants
    private final double screenWidthFactor = 0.75;
    private final double screenHeightFactor = 0.75;

    //Page Widgets
    public Button submitPreferenceButton;
    private EditText cost_max_input;
    private Spinner categoryInput;
    private int cost_max;
    private String category;

    //FireBase Variables
    private FirebaseAuth auth = FirebaseAuth.getInstance();;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();

    //Random
    private String currentGroup;
    private String prefRefStr;
    private String prefrefFirstPart;
    private String prefrefSecondPart;

    //Google vars
    protected GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final int RequestPermissionCode = 1;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        /*
        Display
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*screenWidthFactor), (int)(height * screenHeightFactor));

        /*
        Get Location data
         */
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        /*
        Get Preferences
         */

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(PreferenceActivity.this, MainActivity.class));
        }

        cost_max_input = (EditText) findViewById(R.id.priceInput);
        categoryInput = (Spinner) findViewById(R.id.spinnerCategory);
        submitPreferenceButton = (Button) findViewById(R.id.submitPreference);

        submitPreferenceButton.setOnClickListener(this);

    }
    /*
    submitPreferences function:
        Takes user preferences and stores on FireBase database.
    Procedure:
        1. Get user preferences
        2. Store a new preference on FireBase database
     */
    private void submitPreferences() {

        //Get user Preferences

        String cost_max_string = cost_max_input.getText().toString();

        if (cost_max_string.isEmpty()){
            cost_max_input.setError("Please enter the most your willing to spend. Enter 0 if you're not willing to spend any money");
            cost_max_input.requestFocus();
            return;
        }

        cost_max = Integer.parseInt(cost_max_input.getText().toString());
        category = categoryInput.getSelectedItem().toString();

        //Get current group
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentGroup = documentSnapshot.get("currentGroup").toString();
                /*Toast.makeText( PreferenceActivity.this,
                        "Checkpoint 1 and current group is " + currentGroup,
                        Toast.LENGTH_LONG).show();*/
                db.collection("groups").document(currentGroup)
                        .collection("prefrefs").document(user.getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        prefRefStr = documentSnapshot.get("prefRef").toString();
                        prefrefFirstPart = prefRefStr.substring(0, prefRefStr.length() - 6);
                        prefrefSecondPart = prefRefStr.substring(prefRefStr.length() - 5, prefRefStr.length());
                        Preferences pref = new Preferences(cost_max, category, user.getUid(), longitude, latitude);
                        /*Toast.makeText( PreferenceActivity.this,
                                "Checkpoint 2 and " + prefrefFirstPart + prefrefSecondPart,
                                Toast.LENGTH_LONG).show();*/
                        db.collection(prefrefFirstPart).document(prefrefSecondPart).set(pref).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( PreferenceActivity.this,
                                        "Preferences saved",
                                        Toast.LENGTH_LONG).show();

                                final ProgressDialog dialog = new ProgressDialog(PreferenceActivity.this);
                                dialog.setTitle("Loading...");
                                dialog.setMessage("Please wait. We are generating your recommended activities list. :)");
                                dialog.setIndeterminate(true);
                                dialog.setCancelable(false);
                                dialog.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        dialog.dismiss();
                                        startActivity(new Intent(PreferenceActivity.this, RecListActivity.class));
                                    }
                                }, 10000);

                            }
                        });
                    }
                });

            }
        });

    }

    /*
    Submits Preferences
     */
    public void onClick(View view){
        if (view.equals(submitPreferenceButton)){
            submitPreferences();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            } else {
                                longitude = 0;
                                latitude = 0;
                            }
                        }
                    });
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(PreferenceActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Connection suspended");
    }
}
