package com.example.msplug.utils.connectionchecker;


import android.app.Application;

public class ConnectionCheckerApp extends Application {
    public static ConnectionCheckerApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    public static synchronized ConnectionCheckerApp getInstance(){
        return mInstance;
    }
    public void setConnectivityListener(
            ConnectivityReceiver.ConnectivityReceiverListener listener
    ){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

