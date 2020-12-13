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
    EditText emailID, Password;
    Button btnLogin;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceUtils.getToken(this) != null ){
            Toast.makeText(this, "logged in with token "+PreferenceUtils.getToken(this), Toast.LENGTH_SHORT).show();
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
        Intent Dashboard = new Intent(loginActivity.this, dashboardActivity.class);
        startActivity(Dashboard);

        //initialize the views
        emailID = findViewById(R.id.emailTv);
        Password = findViewById(R.id.passwordTv);
        btnLogin = findViewById(R.id.loginBtn);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String email = emailID.getText().toString();
                String password = Password.getText().toString();
                if (email.isEmpty()){
                    emailID.setError("Please enter Email Address");
                    emailID.requestFocus();
                }
                else if (password.isEmpty()){
                    Password.setError("please enter a valid Password");
                    Password.requestFocus();
                }
                else if (password.isEmpty() && email.isEmpty()){
                    Toast.makeText(loginActivity.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Hardcoding device id for now
                     verifyUser(email, password, "EN1501Q5");
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

                Toast.makeText(loginActivity.this, ""+resp.toString(), Toast.LENGTH_SHORT).show();

                String respJson = resp.toString();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(respJson);
                    JSONObject jsonObjectdata = jsonObject.getJSONObject("data");

                    if (jsonObjectdata != null){
                        String deviceIDs = jsonObjectdata.getString("deviceID");
                        String token = jsonObjectdata.getString("token");
                        String deviceStatus = jsonObjectdata.getString("device_status");

                        //Use device ID for validation
                        if (deviceIDs.equals(deviceID)){
                            if (token != null){
                                PreferenceUtils.saveToken(token, getApplicationContext());
                                Log.d(TAG, "token saved: "+ token);
                            }
                            Toast.makeText(loginActivity.this, "Login Successful"+token, Toast.LENGTH_SHORT).show();
                            Intent Dashboard = new Intent(loginActivity.this, dashboardActivity.class);
                            startActivity(Dashboard);
                            finish();
                        }
                        else{
                            Toast.makeText(loginActivity.this, "Login failed please check your credentials", Toast.LENGTH_SHORT).show();
                        }
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



    //Function to get the device ID
    public static String getDeviceId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return androidId;
    }
}