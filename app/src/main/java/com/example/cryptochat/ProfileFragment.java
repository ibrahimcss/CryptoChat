package com.example.cryptochat;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptochat.DatabaseUserName.AppDatabase;
import com.example.cryptochat.DatabaseUserName.FriendNameChannel;
import com.example.cryptochat.DatabaseUserName.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    AppDatabase appDatabase;
    ListView listView;
    ArrayList<String> requests = new ArrayList<>();
    RequestQueue requestQueue;
    JSONObject json;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        listView = v.findViewById(R.id.listRequest);

        loadUser();
        requestQueue = Volley.newRequestQueue(getActivity());



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                UserPreferences userPreferences = UserPreferences.getInstance(getActivity());
                final String myUserName = userPreferences.getData("username");
                final String friendUserName;
                friendUserName = requests.get(position);
                final String channel=friendUserName+myUserName;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("İSTEK");
                builder.setMessage(friendUserName + " İsteğini Kabul Ediyormusunuz..?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FriendNameChannel  friend=new FriendNameChannel();
                        friend.setUserName(friendUserName);
                        friend.setChannelName(channel);


                        appDatabase = AppDatabase.getAppDatabase(getActivity());
                        appDatabase.friendNameChannelDao().insertAll(friend);
                        appDatabase = AppDatabase.getAppDatabase(getActivity());
                        appDatabase.userDao().delete(friendUserName);

                        loadUser();



                        json = new JSONObject();
                        try {
                            JSONObject userData = new JSONObject();
                            userData.put("title", myUserName);
                            userData.put("body", channel);



                            json.put("data", userData);
                            json.put("to", "/topics/"+friendUserName);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.i("onResponse", "" + response.toString());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("onErrorResponse", "" + error.toString());

                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Authorization", "key=AAAA-lDGoYc:APA91bGFHhFjBniPbwrGamgh0LZq2U8VM_mAgHWmruMe-Z0RZMPxnVo1mAlvMNfjrZ2CL3G1yHGQufvhI5FyD904smz8DN3J0bkjVkVLNYAJ2qL2AgWcXLsi7pHtBuUFpqy-1TFqmu0T");
                                    params.put("Content-Type", "application/json");
                                    return params;
                                }
                            };
                            requestQueue.add(jsonObjectRequest);


                        } catch (JSONException e) {
                            Log.i("JSONException", e.getMessage());

// MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                        }








                        Toast.makeText(getActivity(), "Onaylandı", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appDatabase = AppDatabase.getAppDatabase(getActivity());
                        appDatabase.userDao().delete(friendUserName);

                        loadUser();
                    }
                });

                builder.show();
            }
        });


        return v;
    }

    public void loadUser() {
        requests.clear();
        List<User> list = AppDatabase.getAppDatabase(getActivity()).userDao().getAll();


        for (int i = 0; i < list.size(); i++) {


            requests.add(list.get(i).getFirstName());
            // Toast.makeText(getApplicationContext(),"list item: "+i+": "+list.get(i).getFirstName(),Toast.LENGTH_LONG).show();

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, requests);
        listView.setAdapter(arrayAdapter);

    }


}
