package com.example.cryptochat;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static UserPreferences userPreferences;
    private SharedPreferences sharedPreferences;


    public static UserPreferences getInstance(Context context) {
        if (userPreferences == null) {
            userPreferences = new UserPreferences(context);
        }
        return userPreferences;
    }

    private UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("UserNameSave", Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }
}
