package com.example.cryptochat.Service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.example.cryptochat.ChatMessage;

import com.example.cryptochat.Crypto.AES;
import com.example.cryptochat.Crypto.DiffieHelman;
import com.example.cryptochat.DatabaseUserName.AppDatabase;
import com.example.cryptochat.DatabaseUserName.FriendNameChannel;
import com.example.cryptochat.DatabaseUserName.FriendNameKey;
import com.example.cryptochat.DatabaseUserName.User;
import com.example.cryptochat.MainActivity;
import com.example.cryptochat.R;


import com.example.cryptochat.TopicNotifyApplication;
import com.example.cryptochat.UserKeyPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    PendingIntent pendingIntent;
    Intent intent;
    AppDatabase appDatabase;
    String decrypted = "";
    String uKey;
    int sendUserKey;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getData().size());
        try {
            final Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            String title = object.getString("title");
            String body = object.getString("body");

            if(title.equals("SendUser")){


                sendUserKey=Integer.parseInt(body);



            }



 if(title.equals("mesaj")){
     UserKeyPreferences userKeyPreferences=UserKeyPreferences.getInstance(getApplicationContext());
     int key=userKeyPreferences.getData("username");
     System.out.println("key= "+key);
     DiffieHelman dh=new DiffieHelman();

     BigInteger dhKey=dh.Key(BigInteger.valueOf(13),BigInteger.valueOf(17),BigInteger.valueOf(3));
      String dhKeySha=dh.KeySha(dhKey);

        byte[] mByte= Base64.decode(body,16);


     try {
         decrypted = AES.decrypt(mByte,dhKeySha);
     } catch (Exception e) {
         e.printStackTrace();

     }


     try {

         Log.d("TEST", "decrypted:" + decrypted);

         ChatMessage chatMessage = new ChatMessage();
         chatMessage.setId(1);//dummy
         chatMessage.setMessage(decrypted);
         chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
         chatMessage.setMe(true);


         if (TopicNotifyApplication.getChatActivity() != null) {
             TopicNotifyApplication.getChatActivity().displayMessage(chatMessage);
             TopicNotifyApplication.getChatActivity().setVisible(true);
         }



     } catch (Exception e) {
         e.printStackTrace();
     }


 }


            if (body.equals("Size Arkadaşlık İsteği Gönderdi.")) {


                if (!title.isEmpty()) {
                    sendNotification(title, body, MainActivity.class);

                    User user = new User();
                    user.setFirstName(title);
                    uKey=title;


                    appDatabase = AppDatabase.getAppDatabase(this);
                    appDatabase.userDao().insertAll(user);

                }


            }
            if(body.equals("key")){
                FriendNameKey friendNameKey=new FriendNameKey();
                friendNameKey.setUserName(uKey);
                friendNameKey.setUserkey(title);

                appDatabase = AppDatabase.getAppDatabase(this);
                appDatabase.friendNameKeyDao().insertAll(friendNameKey);
            }

            if(!body.equals("Size Arkadaşlık İsteği Gönderdi.")&&!title.equals("mesaj")&&!body.equals("key")&&!title.equals("SendUser")){

                FriendNameChannel friend=new FriendNameChannel();
                friend.setUserName(title);
                friend.setChannelName(body);


                appDatabase = AppDatabase.getAppDatabase(this);
                appDatabase.friendNameChannelDao().insertAll(friend);

                sendNotification(title, "Arkdaşlık İsteğinizi Onayladı..!", MainActivity.class);
            }



        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    private void sendNotification(String title, String body, Class opClass) {


        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        intent = new Intent(getApplicationContext(), opClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription("Chat Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(style)


                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);


        notificationManager.notify(1, notificationBuilder.build());


    }


}