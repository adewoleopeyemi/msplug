package com.example.msplug.background_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.msplug.R;
import com.example.msplug.dashboard.view.dashboardActivity;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_request_detail.apirequestdetail;
import com.example.msplug.retrofit.endpoints.endpoint_request_detail.requestdetailsbody;
import com.example.msplug.retrofit.endpoints.endpoint_request_list.apirequestlist;
import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.msplug.notification.App.CHANNEL_ID;
import static com.example.msplug.utils.Constants.serviceStatusConstants.ONLINE_STATUS;

public class BackgroundService extends Service {

    Handler handler;
    boolean stillLoading;
    Runnable runnable;
    Handler handlerx;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(4000);
        }
        handlerx.removeCallbacks(runnable);
        //PreferenceUtils.saveStatus("offline", this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg); //no need to change anything here
            }
        };

        String input = intent.getStringExtra(ONLINE_STATUS);
        Intent notificationIntent = new Intent(this, dashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MsPlug")
                .setContentText(input)
                .setSmallIcon(R.drawable.msplugnotificationicon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        handlerx = new Handler();
        final int delay = 20000; // 1000 milliseconds == 1 second


        stillLoading = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                refreshevery15sec();
                handlerx.postDelayed(this, delay);
            }
        };
        runnable.run();
        return START_NOT_STICKY;
    }

    private void refreshevery15sec() {
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apirequestlist requestlist = retrofit.create(apirequestlist.class);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(this));
        Call<requestlistresponse> call = requestlist.getRequestList(headers, PreferenceUtils.getDeviceID(this));
        call.enqueue(new Callback<requestlistresponse>() {
            @Override
            public void onResponse(Call<requestlistresponse> call, Response<requestlistresponse> response) {
                requestlistresponse resp = (requestlistresponse) response.body();
                String requesttype = resp.getRequest_type();
                String sim_slot = resp.getSim_slot();
                String command = resp.getCommand();
                int id = resp.getId();
                Log.d("BackgroundService", "SCS"+ id);
                int device = resp.getDevice();
                String device_name = resp.getDevice_name();
                String recipient = resp.getReceipient();

                if (requesttype!= null){
                    if (requesttype.equals("USSD")) {
                        Toast.makeText(BackgroundService.this, "dialing ussd", Toast.LENGTH_SHORT).show();
                        dialUSSD(sim_slot, command, id);
                        stillLoading = false;
                    }
                    else if (requesttype.equals("SMS")) {
                        Toast.makeText(BackgroundService.this, "sending sms", Toast.LENGTH_SHORT).show();
                        sendSMS(sim_slot, recipient, command, id);
                        stillLoading = false;
                    }
                }

            }

            @Override
            public void onFailure(Call<requestlistresponse> call, Throwable t) {

            }
        });
    }



    @SuppressLint("NewApi")
    private void sendSMS(String sim_slot, String recipient, String command, int id) {
        int position = 0;
        if (sim_slot.equals("sim1")) {
            position = 0;
        } else if (sim_slot.equals("sim2")) {
            position = 1;
        }

        SmsManager smsMan = SmsManager.getDefault();
        smsMan = SmsManager.getSmsManagerForSubscriptionId(position);
        smsMan.sendTextMessage(recipient, null, command, null, null);
        updaterequestdetails(command + " sent to " + recipient + "successful", "completed", id);
    }


    @SuppressLint("MissingPermission")
    private void dialUSSD(String sim_slot, String command, int id) {
        int position = 0;
        if (sim_slot.equals("sim1")) {
            position = 0;
        } else if (sim_slot.equals("sim2")) {
            position = 1;
        }
        runUssdCode(command, position, id);
    }


    //Comenack here and remove toast message for oResponse
    private void updaterequestdetails(String response_message, String status, int requestID){
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apirequestdetail requestdetails = retrofit.create(apirequestdetail.class);
        requestdetailsbody body = new requestdetailsbody();

        body.setResponse_message(response_message);
        body.setStatus(status);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(this));
        Call<requestlistresponse> call = requestdetails.updateRequestDetails(headers, requestID, body);
        call.enqueue(new Callback<requestlistresponse>() {
            @Override
            public void onResponse(Call<requestlistresponse> call, Response<requestlistresponse> response) {
                requestlistresponse resp = (requestlistresponse) response.body();
            }

            @Override
            public void onFailure(Call<requestlistresponse> call, Throwable t) {
            }
        });
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void runUssdCode(String ussd, int position, int id) {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        try{
            manager.createForSubscriptionId(position);
            return;
        }
        catch (Exception e){
        }
        Log.d("networkprovider",manager.getNetworkOperatorName());
            manager.sendUssdRequest(ussd, new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Log.d("BackgroundService", "onReceiveUssdResponse: "+response.toString());
                    updaterequestdetails(response.toString(), "completed", id);
                    PreferenceUtils.saveUpdateStatus("completed", getApplicationContext());
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    updaterequestdetails("request failed "+request.toString(), "failed", id);
                    Log.d(this.getClass().getName(), "response" + failureCode);
                    PreferenceUtils.saveUpdateStatus("failed", getApplicationContext());
                }
            }, new Handler());
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}