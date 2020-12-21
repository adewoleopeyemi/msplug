package com.example.msplug.auth.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.msplug.R;
import com.example.msplug.dashboard.view.dashboardActivity;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_login.apilogin;
import com.example.msplug.retrofit.endpoints.endpoint_login.loginresponse;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class loginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    EditText emailID, Password, deviceID;
    Button btnLogin;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceUtils.getToken(this) != null ){
            Toast.makeText(this, "Token: "+PreferenceUtils.getToken(this)+ "Device ID: "+PreferenceUtils.getDeviceID(this), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(loginActivity.this, dashboardActivity.class);
            startActivity(intent);
        }else{
        }
        setContentView(R.layout.activity_login);
        //set StatusBar to Blue
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorComedyBlue));
        }

        getSupportActionBar().hide();
        //initialize the views
        emailID = findViewById(R.id.emailTv);
        Password = findViewById(R.id.passwordTv);
        btnLogin = findViewById(R.id.loginBtn);
        deviceID = findViewById(R.id.deviceIDTv);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                String password = Password.getText().toString();
                String device_ID = deviceID.getText().toString();
                if (password.isEmpty() && email.isEmpty() && device_ID.isEmpty()){
                    Toast.makeText(loginActivity.this, "Fields are Empty", Toast.LENGTH_LONG).show();
                }

                else if (device_ID.isEmpty()){
                    deviceID.setError("please enter a valid Password");
                    deviceID.requestFocus();
                }

                else if (email.isEmpty()){
                    emailID.setError("Please enter Email Address");
                    emailID.requestFocus();
                }
                else if (password.isEmpty()){
                    Password.setError("please enter a valid Password");
                    Password.requestFocus();
                }

                else {
                     pd.show();
                     verifyUser(email, password, device_ID);
                }
            }
        });
    }

    private void verifyUser(String email, String password, final String deviceID) {
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apilogin login = retrofit.create(apilogin.class);

        Call<loginresponse> call = login.verifyUser(email, password, deviceID);
        call.enqueue(new Callback<loginresponse>() {
            @Override
            public void onResponse(Call<loginresponse> call, Response<loginresponse> response) {
                pd.dismiss();
                loginresponse resp = (loginresponse) response.body();

                //Toast.makeText(loginActivity.this, ""+resp.toString(), Toast.LENGTH_SHORT).show();

                String respJson = resp.toString();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(respJson);
                    String message = jsonObject.getString("message");
                    JSONObject jsonObjectdata = jsonObject.getJSONObject("data");
                    Toast.makeText(loginActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    if (jsonObjectdata != null){
                        String deviceIDs = jsonObjectdata.getString("deviceID");
                        String token = jsonObjectdata.getString("token");
                        String deviceStatus = jsonObjectdata.getString("device_status");

                        //Use device ID for validation
                        if (deviceIDs.equals(deviceID)){
                            if (token != null){
                                PreferenceUtils.saveToken(token, getApplicationContext());
                                PreferenceUtils.saveDeviceID(deviceIDs, getApplicationContext());
                                Log.d(TAG, "token saved: "+ token);
                            }
                            Toast.makeText(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent Dashboard = new Intent(loginActivity.this, dashboardActivity.class);
                            startActivity(Dashboard);
                            finish();
                        }
                        else{
                            Toast.makeText(loginActivity.this, "Login unsuccessful" , Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "xErrorEncountered: "+e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<loginresponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}