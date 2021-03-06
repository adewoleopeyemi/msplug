package com.example.msplug.utils.sharedPrefences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.msplug.utils.sharedPrefences.Constants;


public class PreferenceUtils {

    public PreferenceUtils(){

    }

    public static boolean saveToken(String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.KEY_TOKEN, token);
        prefsEditor.apply();
        return true;
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.KEY_TOKEN, null);
    }


    public static boolean saveStatus(String connected, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.IS_CONNECTED, connected);
        prefsEditor.apply();
        return true;
    }

    public static String getStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.IS_CONNECTED, null);
    }
    public static boolean saveUpdateStatus(String Status, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.STATUS, Status);
        prefsEditor.apply();
        return true;
    }

    public static String getUpdateStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.STATUS, "");
    }

    public static boolean saveDeviceID(String device_ID, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.DEVICE_ID, device_ID);
        prefsEditor.apply();
        return true;
    }

    public static String getDeviceID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.DEVICE_ID, null);
    }

    public static boolean saveIsFirstTimeUser(String isFirstTimeUser, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.IS_FIRST_TIME_USER, isFirstTimeUser);
        prefsEditor.apply();
        return true;
    }
    public static String getIsFirstTimeUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.IS_FIRST_TIME_USER, null);
    }

    public static boolean savePrevRequestId(String id, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.CUR_REQUEST, id);
        prefsEditor.apply();
        return true;
    }
    public static String getPrevRequestId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.CUR_REQUEST, null);
    }

    public static boolean savePrevResponse(String message, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.CUR_REPONSE_MESSAGE, message);
        prefsEditor.apply();
        return true;
    }
    public static String getPrevResponse(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.CUR_REPONSE_MESSAGE, "");
    }

}
