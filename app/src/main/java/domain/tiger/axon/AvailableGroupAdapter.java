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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableGroupAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnJoin;
    private TextView groupName;
    private int i;
    private boolean done;

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
        groupName = (TextView) view.findViewById(R.id.list_item_groupName);
        groupName.setText(list.get(position));

        btnJoin = (Button) view.findViewById(R.id.btnJoin);

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
                            i = 1;
                            done = false;

                            for (; i < 9; i++){
                                db.collection("groups").document(list.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        System.out.println("debug " + Boolean.valueOf(documentSnapshot.get("pref"+i).toString()));
                                        /*if (!((boolean) documentSnapshot.get("pref" + i)) && done == false){
                                            /*Preferences pref = new Preferences();
                                            db.collection("groups/" + list.get(position) + "/prefs").document("pref"+i).set(pref);
                                            db.collection("groups").document(list.get(position)).update("pref"+i, (boolean) true);
                                            done = true;
                                        }*/
                                    }
                                });
                                if (done == true){
                                    break;
                                }
                            }

                            //Obtain reference to newly created reference
                            String prefRefStr = "groups/" + list.get(position) + "/prefs/pref" + i;

                            //Store preference reference
                            Map<String, String> prefRefMap = new HashMap<>();
                            FirebaseUser user = auth.getCurrentUser();
                            prefRefMap.put("prefRef", prefRefStr);
                            db.collection("groups/" + list.get(position) + "/prefrefs").document(user.getUid()).set(prefRefMap);

                            //Update size
                            size++;
                            db.collection("groups").document(list.get(position)).update("size", size);

                            //Update user information to include group
                            DocumentReference groupRef = db.collection("groups").document(list.get(position));
                            UserGroup userGroup = new UserGroup(groupRef, list.get(position));
                            db.collection("users/" + user.getUid()+ "/groups").document(list.get(position)).set(userGroup);

                            //Add user to group
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("usid", (String) user.getUid());
                            userMap.put("admin", (boolean) false);
                            db.collection("groups").document(list.get(position)).collection("users").document(user.getUid()).set(userMap);
                            list.remove(position);
                            AvailableGroupAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        return view;
    }


}
