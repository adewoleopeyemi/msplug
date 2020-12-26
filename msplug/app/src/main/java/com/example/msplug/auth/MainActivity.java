package com.example.msplug.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.msplug.R;
import com.example.msplug.auth.instructions.instructions;
import com.example.msplug.auth.login.loginActivity;
import com.example.msplug.dashboard.view.dashboardActivity;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    private Animation animate;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set StatusBar to Black
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        //Hides action bar
        getSupportActionBar().hide();
        //Delays activity for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtils.getIsFirstTimeUser(getApplicationContext()) == null ){
                    Intent i=new Intent(MainActivity.this, instructions.class);
                    startActivity(i);
                }
                else{
                    Intent i=new Intent(MainActivity.this, loginActivity.class);
                    startActivity(i);
                }
            }
        }, 2000);
    }
}