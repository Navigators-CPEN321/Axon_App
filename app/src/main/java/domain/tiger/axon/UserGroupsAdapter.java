package domain.tiger.axon;

import android.content.Context;
import android.content.Intent;
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

public class UserGroupsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    public UserGroupsAdapter(ArrayList<String> list, Context context){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_user_groups, null);
        }
        final TextView groupName = (TextView) view.findViewById(R.id.list_item_groupName);
        groupName.setText(list.get(position));

        Button btnView = (Button) view.findViewById(R.id.btnViewGroup);

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid()).update("currentGroup", list.get(position));
                Intent intent = new Intent(context, GroupViewActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }

}
