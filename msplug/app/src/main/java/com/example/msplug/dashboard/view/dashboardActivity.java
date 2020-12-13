package com.example.msplug.dashboard.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.msplug.R;
import com.example.msplug.dashboard.home.homeFragment;
import com.example.msplug.dashboard.messages.messagesFragment;
import com.example.msplug.utils.connectionchecker.ConnectivityReceiver;
import com.example.msplug.utils.connectionchecker.InternetConnectionService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboardActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    LottieAnimationView networkIndicatorAnim;
    public static final String BroadcastStringForAction = "checkinternet";
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorComedyBlue));
        }
        getSupportActionBar().hide();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadcastStringForAction);
        Intent service = new Intent(this, InternetConnectionService.class);
        startService(service);




        homeFragment fragment1 = new homeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();

        networkIndicatorAnim = findViewById(R.id.noInternetConnection);
        networkIndicatorAnim.playAnimation();


        if (!isOnline(getApplicationContext())) {
            networkIndicatorAnim.setVisibility(View.VISIBLE);

        } else {
            networkIndicatorAnim.setVisibility(View.GONE);
        }
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.action_home:
                            homeFragment fragment1 = new homeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;

                        case R.id.action_message:
                            messagesFragment fragment2 = new messagesFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;
                    }

                    return false;
                }
            };


    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            return true;
        } else
            return false;
    }

    public BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastStringForAction)) {
                if (intent.getStringExtra("online_status").equals("true")) {
                    networkIndicatorAnim.setVisibility(View.GONE);
                } else {
                    networkIndicatorAnim.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(MyReceiver, mIntentFilter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            networkIndicatorAnim.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(MyReceiver, mIntentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}