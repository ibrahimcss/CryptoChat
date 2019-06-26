package com.example.cryptochat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptochat.Crypto.DiffieHelman;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendFragment extends Fragment {

    ImageButton ib;
    EditText et;
    String searchText;
    ListView sonucList;
    String namePut;
    String requestName;


    private ArrayList<String> kullanicilar = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");


    JSONObject json;
    RequestQueue requestQueue;

    public SearchFriendFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_friend, container, false);
        mAuth = FirebaseAuth.getInstance();
        ib = v.findViewById(R.id.imageButton);
        et = v.findViewById(R.id.editText);


        requestQueue = Volley.newRequestQueue(getActivity());

        sonucList = v.findViewById(R.id.sonucList);
        ib.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if (!et.getText().toString().equals("")) {

                    myRef.orderByChild("username").equalTo(searchText = et.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {


                                kullanicilar.clear();
                                kullanicilar.add(childDataSnapshot.child("username").getValue().toString());
                                namePut = childDataSnapshot.child("username").getValue().toString();

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kullanicilar);
                                sonucList.setAdapter(arrayAdapter);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });

                }


            }
        });


        sonucList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
              requestName=et.getText().toString();
                UserPreferences userPreferences = UserPreferences.getInstance(getActivity());
                String uname = userPreferences.getData("username");

                String istek = uname;
                String body = "Size Arkadaşlık İsteği Gönderdi.";
                json = new JSONObject();
                try {
                    JSONObject userData = new JSONObject();
                    userData.put("title", istek);
                    userData.put("body", body);


                    json.put("data", userData);
                    json.put("to", "/topics/"+requestName);
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

                Toast.makeText(getActivity(),"İstek Gönderildi",Toast.LENGTH_LONG).show();
                UserKeyPreferences userKeyPreferences = UserKeyPreferences.getInstance(getActivity());
                int mKey  = userKeyPreferences.getData("username");
                DiffieHelman dh=new DiffieHelman();
                 BigInteger myKey=dh.Key(BigInteger.valueOf(mKey),BigInteger.valueOf(17),BigInteger.valueOf(3));
                 int key=myKey.intValue();
                String icerik = "key";
                JSONObject json2 = new JSONObject();
                try {
                    JSONObject userData = new JSONObject();
                    userData.put("title", key);
                    userData.put("body", icerik);


                    json2.put("data", userData);
                    json2.put("to", "/topics/"+requestName);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", json2, new Response.Listener<JSONObject>() {
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





                return true;
            }
        });


        return v;
    }

}
