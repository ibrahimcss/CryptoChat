package com.example.cryptochat;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cryptochat.DatabaseUserName.AppDatabase;
import com.example.cryptochat.DatabaseUserName.FriendNameChannel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {

    ListView listViewchat;
    ArrayList<String> requests = new ArrayList<>();
    String friendName;
    AppDatabase appDatabase;

    public FriendListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_friend_list, container, false);
        listViewchat=v.findViewById(R.id.listviewchat);
        loadUser();


        listViewchat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendName=requests.get(position);
                appDatabase = AppDatabase.getAppDatabase(getActivity());
                String friendChannel=appDatabase.friendNameChannelDao().findByName(friendName).getChannelName();
                Toast.makeText(getActivity(),friendChannel,Toast.LENGTH_LONG).show();
                FirebaseMessaging.getInstance().subscribeToTopic(friendChannel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                String msg = "succesfull";
                                if (!task.isSuccessful()) {
                                    msg = "fail";

                                }
                                Toast.makeText(getActivity(),"bu kanal : "+ msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                Intent i=new Intent(getActivity(),ChatActivity.class);

                i.putExtra("friendname",friendName);




                startActivity(i);
            }
        });

        listViewchat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String friendUserName;
                friendUserName = requests.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("SİL");
                builder.setMessage(friendUserName + "Silmek İstiyormusunuz..?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        appDatabase = AppDatabase.getAppDatabase(getActivity());
                        appDatabase.friendNameKeyDao().delete(friendUserName);
                        appDatabase=AppDatabase.getAppDatabase(getActivity());
                        appDatabase.friendNameChannelDao().delete(friendUserName);
                        appDatabase.friendNameChannelDao().delete(friendUserName);

                        loadUser();
                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUser();
                    }
                });
                builder.show();

                return true;
            }
        });


        return  v;
    }
    public void loadUser() {
        requests.clear();
        List<FriendNameChannel> list = AppDatabase.getAppDatabase(getActivity()).friendNameChannelDao().getAll();


        for (int i = 0; i < list.size(); i++) {


            requests.add(list.get(i).getUserName());
            // Toast.makeText(getApplicationContext(),"list item: "+i+": "+list.get(i).getUserName(),Toast.LENGTH_LONG).show();

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, requests);
        listViewchat.setAdapter(arrayAdapter);

    }

}
