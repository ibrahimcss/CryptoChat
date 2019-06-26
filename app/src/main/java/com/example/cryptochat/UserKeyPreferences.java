package com.example.cryptochat;

import android.content.Context;
import android.content.SharedPreferences;

public class UserKeyPreferences {
    private static UserKeyPreferences userKeyPreferences;
    private SharedPreferences sharedPreferences;


    public static UserKeyPreferences getInstance(Context context) {
        if (userKeyPreferences == null) {
            userKeyPreferences = new UserKeyPreferences(context);
        }
        return userKeyPreferences;
    }

    private UserKeyPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("UserKeySave", Context.MODE_PRIVATE);
    }

    public void saveData(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public int getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 1);
        }
        return 1;
    }
}
