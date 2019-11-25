package com.example.mox;

import android.content.Context;
import android.content.SharedPreferences;

public class Sp {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Sp(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_manhole",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void put(final String key, final String value) {
        editor.putString(key, value).apply();
    }

    public String getString(final  String key) {
        return sharedPreferences.getString(key, "");
    }


}
