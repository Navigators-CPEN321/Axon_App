package domain.tiger.axon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/*
Displays a ListView of the categories that the user can browse in the events catalog
 */
public class EventsCatalogActivity extends AppCompatActivity {

    //Bottom navigation vars
    private TextView mTextMessage;

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //ListView vars
    private ListView lvEvents;
    private ArrayList<String> eventsArrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    /*
    Bottom navigation bar functionality
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(EventsCatalogActivity.this, GroupNavigationActivity.class));
                    return true;
                case R.id.navigation_dashboard:
                    Intent i = new Intent(EventsCatalogActivity.this, EventsCatalogActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(EventsCatalogActivity.this, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    /*
    Displays drop-down menu on actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /*
    Provides functionality to drop-down menu on actionbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.invitations:
                startActivity(new Intent(EventsCatalogActivity.this, InvitationsActivity.class));
                break;
            case R.id.logout:
                Intent intent = new Intent(EventsCatalogActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                auth.signOut();
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_catalog);

        //Display bottom navigation bar
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Set up ListView
        lvEvents = (ListView) findViewById(R.id.lvEventsList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventsArrayList);
        lvEvents.setAdapter(adapter);

        //Add the categories for user to view
        eventsArrayList.add("Food & Drink");
        eventsArrayList.add("Sports & Active Life");
        eventsArrayList.add("Nightlife");
        eventsArrayList.add("Festivals & Fairs");
        eventsArrayList.add("Arts");

        //Sort and notify adapter of the categories we added
        Collections.sort(eventsArrayList, String.CASE_INSENSITIVE_ORDER);
        adapter.notifyDataSetChanged();

        //Update the user information to contain the category they want to view and go to EventListActivity
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db.collection("users").document(user.getUid())
                        .update("mostRecentEventsList", eventsArrayList.get(position));
                startActivity(new Intent(EventsCatalogActivity.this, EventListActivity.class));
            }
        });

    }

}
