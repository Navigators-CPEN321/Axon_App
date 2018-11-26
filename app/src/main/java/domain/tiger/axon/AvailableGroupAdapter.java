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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Adapter used to fill the GroupAvailableActivity ListView.
The ListView contains a list of groups that are available for users to join.
Each item in the ListView has a group name and a join button, which they can use to join the group.
 */
public class AvailableGroupAdapter extends BaseAdapter implements ListAdapter {

    //AvailableGroupAdapter Vars
    private ArrayList<String> list;
    private Context context;

    //Firebase vars
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    //Other
    private int i; //Used to keep track of which preference. Needs to be global because used inside of another method.

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

        //Connect the TextView and Button
        TextView groupName = (TextView) view.findViewById(R.id.list_item_groupName);
        Button btnJoin = (Button) view.findViewById(R.id.btnJoin);

        //Set the ListView text to the group name
        groupName.setText(list.get(position));

        //Functionality for joining the group
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference ref = db.collection("groups").document(list.get(position));
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //Get document snapshot of group the user wants to group
                            DocumentSnapshot doc = task.getResult();

                            //Get size of the group and update
                            String size_str = doc.get("size").toString();
                            int size = Integer.parseInt(size_str) + 1;
                            db.collection("groups").document(list.get(position)).update("size", size);

                            //Update user information to include group
                            DocumentReference groupRef = db.collection("groups").document(list.get(position));
                            UserGroup userGroup = new UserGroup(groupRef, list.get(position));
                            db.collection("users/" + user.getUid()+ "/groups").document(list.get(position)).set(userGroup);

                            //Update group information to include user
                            db.collection("groups").document(list.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    //Check for the smallest available preference
                                    for (i = 1; i < 9; i++){
                                        if (!((boolean) documentSnapshot.get("pref" + i))){
                                            Preferences pref = new Preferences();
                                            db.collection("groups/" + list.get(position) + "/prefs").document("pref"+i).set(pref);
                                            db.collection("groups").document(list.get(position)).update("pref"+i,  true);
                                            break;
                                        }
                                    }

                                    //Create reference to newly created preference and store it in the database
                                    String prefRefStr = "groups/" + list.get(position) + "/prefs/pref" + i;
                                    Map<String, String> prefRefMap = new HashMap<>();
                                    prefRefMap.put("prefRef", prefRefStr);
                                    db.collection("groups/" + list.get(position) + "/prefrefs").document(user.getUid()).set(prefRefMap);

                                    //Add user to group
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("usid", (String) user.getUid());
                                    userMap.put("admin", (boolean) false);
                                    userMap.put("email", (String) user.getEmail());
                                    db.collection("groups").document(list.get(position)).collection("users").document(user.getUid()).set(userMap);
                                    list.remove(position);
                                    AvailableGroupAdapter.this.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }


}
