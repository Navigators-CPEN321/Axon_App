package domain.tiger.axon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InvitationsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean partOfGroup;
    private int i;
    private boolean done;
    private int numPrefs = 9;
    private int size;

    public InvitationsAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_invitations, null);
        }
        TextView groupName = view.findViewById(R.id.inviteInfo);
        groupName.setText(list.get(position));

        Button join = (Button) view.findViewById(R.id.btnJoinGroup);
        Button reject = (Button) view.findViewById(R.id.btnReject);


        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid())
                        .collection("groups").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //Check if the user is part of the group
                        List<DocumentSnapshot> qsList = queryDocumentSnapshots.getDocuments();
                        partOfGroup = false;
                        for (int i = 0; i < qsList.size(); i++){
                            if (qsList.get(i).get("group_name").equals(list.get(position))){

                                partOfGroup = true;
                                break;
                            }
                        }

                        //If they're not part of the group add them to group and remove invite
                        if (!partOfGroup){
                            addUserToGroup(position);
                            Toast.makeText(context,
                                    "Join successful",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(context,
                                    "You're already part of the group!",
                                    Toast.LENGTH_LONG).show();
                            list.remove(position);
                            InvitationsAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid()).collection("invitations").document(list.get(position)).delete();
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void addUserToGroup(final int position) {
        db.collection("groups").document(list.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                done = false;
                //Search for smallest unused preference and create a preference
                for (i = 1; i < numPrefs; i++){
                    if (!((boolean) documentSnapshot.get("pref" + i)) && done == false){
                        Preferences pref = new Preferences();
                        db.collection("groups/" + list.get(position) + "/prefs").document("pref"+i).set(pref);
                        db.collection("groups").document(list.get(position)).update("pref"+i,  true);

                        done = true;
                        break;
                    }
                }

                //Create reference to newly created reference
                String prefRefStr = "groups/" + list.get(position) + "/prefs/pref" + i;

                //Store preference reference
                Map<String, String> prefRefMap = new HashMap<>();
                prefRefMap.put("prefRef", prefRefStr);
                db.collection("groups/" + list.get(position) + "/prefrefs").document(user.getUid()).set(prefRefMap);

                //Add user to group
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("usid", (String) user.getUid());
                userMap.put("admin", (boolean) false);
                userMap.put("email", (String) user.getEmail());
                db.collection("groups").document(list.get(position)).collection("users").document(user.getUid()).set(userMap);

                //Update user information to include group
                DocumentReference groupRef = db.collection("groups").document(list.get(position));
                UserGroup userGroup = new UserGroup(groupRef, list.get(position));
                db.collection("users/" + user.getUid()+ "/groups").document(list.get(position)).set(userGroup);

                db.collection("groups").document(list.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        size = Integer.valueOf(documentSnapshot.get("size").toString()) + 1;
                        db.collection("groups").document(list.get(position)).update("size", size);

                        db.collection("users").document(user.getUid()).collection("invitations").document(list.get(position)).delete();

                        list.remove(position);
                        InvitationsAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
