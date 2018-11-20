package domain.tiger.axon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class EventsCatalogActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView lvEvents;
    private ArrayList<String> eventsArrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(EventsCatalogActivity.this, GroupNavigationActivity.class));
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(EventsCatalogActivity.this, EventsCatalogActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(EventsCatalogActivity.this, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_catalog);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        lvEvents = (ListView) findViewById(R.id.lvEventsList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventsArrayList);
        lvEvents.setAdapter(adapter);

        eventsArrayList.add("Music");
        eventsArrayList.add("Visual Arts");
        eventsArrayList.add("Performing Arts");
        eventsArrayList.add("Film");
        eventsArrayList.add("Lectures & Books");
        eventsArrayList.add("Fashion");
        eventsArrayList.add("Festivals & Fairs");
        eventsArrayList.add("Charities");
        eventsArrayList.add("Sports & Active Life");
        eventsArrayList.add("Nightlife");
        eventsArrayList.add("Kids & Family");
        eventsArrayList.add("Other");

        Collections.sort(eventsArrayList, String.CASE_INSENSITIVE_ORDER);
        adapter.notifyDataSetChanged();

    }

}
