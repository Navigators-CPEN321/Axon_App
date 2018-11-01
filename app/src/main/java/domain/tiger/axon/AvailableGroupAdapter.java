package domain.tiger.axon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableGroupAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public AvailableGroupAdapter(ArrayList<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int pos){
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_item, null);
        }
        final TextView groupName = (TextView) view.findViewById(R.id.list_item_groupName);
        groupName.setText(list.get(position));

        Button btnJoin = (Button) view.findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference ref = db.collection("groups").document(list.get(position));
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            String size_str = doc.get("size").toString();
                            int size = Integer.parseInt(size_str);

                            //Add personal preference
                            Preferences pref = new Preferences();
                            db.collection("groups/" + list.get(position) + "/prefs").document("pref"+(size+1)).set(pref);

                            //Obtain reference to newly created reference
                            DocumentReference prefRef;
                            prefRef = db.collection("groups/" + list.get(position) + "/prefs").document("pref"+(size+1));

                            //Store preference reference
                            Map<String, DocumentReference> prefRefMap = new HashMap<>();
                            FirebaseUser user = auth.getCurrentUser();
                            prefRefMap.put("Reference", prefRef);
                            db.collection("groups/" + list.get(position) + "/prefrefs").document(user.getUid()).set(prefRefMap);

                            //Update size
                            size++;
                            db.collection("groups").document(list.get(position)).update("size", size);
                        }
                    }
                });
            }
        });

        return view;
    }
}
