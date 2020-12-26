package com.example.msplug.auth.instructions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.example.msplug.R;
import com.example.msplug.auth.MainActivity;
import com.example.msplug.auth.instructions.adapters.adapter;
import com.example.msplug.auth.instructions.models.Model;
import com.example.msplug.auth.login.loginActivity;

import java.util.ArrayList;
import java.util.List;

public class instructions extends AppCompatActivity {
    ViewPager viewPager;
    adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    @SuppressLint("RestrictedApi")
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        models = new ArrayList<>();
        getSupportActionBar().hide();
        String descWelcome = "Welcome to Msplug\nBest Platform for Automated VTU Business\n" +
                "Automate Your VTU Data Platform With Your Own Sim. Dispense SME & Gifting Data/Airtime Automatically From Your Sim To Your Customers Using Our Well Crafted Infrastructure.";
        String descGuidelines = "To get the best of our app we recommend that you put off battery saver mode and also disable optimization. Thank you for trusting us";
        models.add(new Model("Welcome", descWelcome));
        models.add(new Model("GuideLines", descGuidelines));
        adapter = new adapter(models, getApplicationContext());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        //viewPager.setPadding(130, 0, 130, 0);

        button = findViewById(R.id.btnTonext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(instructions.this, loginActivity.class);
                startActivity(i);
            }
        });
        Integer[] colors_temp = {
                getResources().getColor(R.color.colorComedyBlue),
                getResources().getColor(R.color.colorComedyBlue),
                getResources().getColor(R.color.colorFadeBlue),
        };
        colors = colors_temp;
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == (adapter.getCount()-1) && position < (colors.length-1)){
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position+1]));
                }
                else{
                    viewPager.setBackgroundColor(colors[colors.length-1]);

                }
                if (position == (adapter.getCount()-1)){
                    button.setText("Finish");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}