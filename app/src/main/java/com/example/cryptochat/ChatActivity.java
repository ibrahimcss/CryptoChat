package com.example.cryptochat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptochat.Crypto.AES;
import com.example.cryptochat.Crypto.DiffieHelman;
import com.example.cryptochat.DatabaseUserName.AppDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    JSONObject json;
    RequestQueue requestQueue;
byte[] encrypted ;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    TextView melabel;
    TextView frindName;

    Intent intent;
    AppDatabase appDatabase;

    String body = "";
    String meName;
    String frName;
    String friendChannel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("MESAJLAŞMA");

        initControls();
        messageET=findViewById(R.id.messageEdit);
        melabel=findViewById(R.id.meNameLabel);
        frindName=findViewById(R.id.friendNameLabel);
        UserPreferences userPreferences = UserPreferences.getInstance(getApplicationContext());
         meName = userPreferences.getData("username");
         melabel.setText(meName);
         intent=getIntent();
        frName=intent.getStringExtra("friendname");
         frindName.setText(frName);
        appDatabase = AppDatabase.getAppDatabase(this);
        friendChannel=appDatabase.friendNameChannelDao().findByName(frName).getChannelName();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("Notification,", "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            body = manager.getActiveNotifications().toString();

        }

        requestQueue = Volley.newRequestQueue(this);

        TopicNotifyApplication.setChatActivity(this);

    }

    @Override
    protected void onDestroy() {
        TopicNotifyApplication.setChatActivity(null);

        super.onDestroy();
    }

    boolean isDone = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDone) {
            isDone = true;
            String title = getIntent().getStringExtra("title");
            String body = getIntent().getStringExtra("messageBody");
            if (title != null) {
                Toast.makeText(this, body, Toast.LENGTH_LONG).show();
                ChatMessage message = new ChatMessage();
                message.setMessage(body);
                message.setMe(true);
                displayMessage(message);
            }
        }

    }

    private void initControls() {
        messagesContainer = findViewById(R.id.messagesContainer);
        messageET = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.chatSendButton);



        loadDummyHistory();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = messageET.getText().toString();
                appDatabase = AppDatabase.getAppDatabase(getApplicationContext());



                UserKeyPreferences userKeyPreferences=UserKeyPreferences.getInstance(getApplicationContext());
                int key=userKeyPreferences.getData("username");
                DiffieHelman dh=new DiffieHelman();

                BigInteger dhKey=dh.Key(BigInteger.valueOf(key),BigInteger.valueOf(17),BigInteger.valueOf(3));


                String dhKeyStr=dh.KeySha(dhKey);


                try {
                    encrypted = AES.encrypt(messageText,dhKeyStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                JSONObject json2 = new JSONObject();
                try {

                    JSONObject userData = new JSONObject();
                    userData.put("title", "SendUser");
                    userData.put("body", key);

                    json2.put("data", userData);
                    json2.put("to", "/topics/"+friendChannel);
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


                }
// Message send

                json = new JSONObject();
                try {

                    body=messageET.getText().toString();
                    JSONObject userData = new JSONObject();
                    userData.put("title", "mesaj");
                    userData.put("body", Base64.encodeToString(encrypted,16));

                    json.put("data", userData);
                    json.put("to", "/topics/"+friendChannel);
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

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(false);

                messageET.setText("");

                displayMessage(chatMessage);
            }
        });


    }

    public void displayMessage(final ChatMessage message) {
        messagesContainer.post(new Runnable() {
            @Override
            public void run() {
                adapter.add(message);
                adapter.notifyDataSetChanged();
                scroll();
            }
        });

    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    public void loadDummyHistory() {


        chatHistory = new ArrayList<ChatMessage>();
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage(body);
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        if (!body.equals("")) {
            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);
                displayMessage(message);
            }

        }


    }

}