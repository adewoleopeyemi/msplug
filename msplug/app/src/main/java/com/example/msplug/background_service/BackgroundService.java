package com.example.msplug.background_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
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
import com.example.msplug.retrofit.endpoints.endpoint_request_list.body;
import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.example.msplug.notification.NotificationHelper;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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
    TelephonyManager manager;
    TelephonyManager.UssdResponseCallback ussdResponseCallback;
    private NotificationHelper helper;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg); //no need to change anything here
            }
        };
        helper = new NotificationHelper(this);

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
        Boolean send = false;

        startForeground(1, notification);


        handlerx = new Handler();
        final int delay = 50000; // 1000 milliseconds == 1 second

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

    boolean updated = false;
    private void refreshevery15sec() {
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apirequestlist requestlist = retrofit.create(apirequestlist.class);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(this));
        Call<body> call = requestlist.getRequestList(headers, PreferenceUtils.getDeviceID(this));
        call.enqueue(new Callback<body>() {
            @Override
            public void onResponse(Call<body> call, Response<body> response) {
                body resp = (body) response.body();

                requestlistresponse d = resp.getData();

                    String requesttype = d.getRequest_type();
                    String sim_slot = d.getSim_slot();
                    String command = d.getCommand();
                    int id = d.getId();
                    String recipient = d.getReceipient();




                    //Logic Used to handle issues associated with Airtel
                    if (PreferenceUtils.getPrevRequestId(BackgroundService.this) != null){
                        int prevReqId = Integer.parseInt(PreferenceUtils.getPrevRequestId(BackgroundService.this));
                        if (prevReqId == id){
                            updaterequestdetails(PreferenceUtils.getPrevResponse(BackgroundService.this), "completed", id);
                            //Used to handle a case where ussd has dialed and network problem happens immediately afterwards
                            if (updated){
                                PreferenceUtils.savePrevResponse("", BackgroundService.this);
                                PreferenceUtils.savePrevRequestId(null, BackgroundService.this);
                                PreferenceUtils.saveUpdateStatus("", BackgroundService.this);
                                updated = false;
                            }
                        }

                        else {
                            if (requesttype != null) {
                                if (requesttype.equals("USSD")) {
                                    dialUSSD(sim_slot, command, id);
                                    stillLoading = false;
                                } else if (requesttype.equals("SMS")) {
                                    sendSMS(sim_slot, recipient, command, id);
                                    stillLoading = false;
                                }
                            }
                        }
                    }
                    else {
                        if (requesttype != null) {
                            if (requesttype.equals("USSD")) {
                                dialUSSD(sim_slot, command, id);
                                stillLoading = false;
                            } else if (requesttype.equals("SMS")) {
                                sendSMS(sim_slot, recipient, command, id);
                                stillLoading = false;
                            }
                        }
                    }
            }
            @Override
            public void onFailure(Call<body> call, Throwable t) {

            }
        });
    }




    @SuppressLint("NewApi")
    private void sendSMS(String sim_slot, String recipient, String command, int id) {
        int position = 1;
        if (sim_slot.equals("sim1")) {
            position = 1;
        } else if (sim_slot.equals("sim2")) {
            position = 2;
        }

        
        SmsManager smsMan = SmsManager.getDefault();
        SmsManager.getSmsManagerForSubscriptionId(position).sendTextMessage(recipient, null, command, null, null);;
        updaterequestdetails(command + " sent to " + recipient + " successful ", "completed", id);
        sendNotification(command + " sent to " + recipient + " successful", "SMS");
        PreferenceUtils.savePrevRequestId(String.valueOf(id), BackgroundService.this);
        PreferenceUtils.savePrevResponse(command + " sent to " + recipient + " successful", BackgroundService.this);
    }


    @SuppressLint({"MissingPermission","NewApi", "LocalSuppress"})
    private void dialUSSD(String sim_slot, String command, int id) {
        List<SubscriptionInfo> subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();

        for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Log.d("Sims", "subscriptionId:" + subscriptionId);
        }
        int sim1 = subscriptionInfos.get(0).getSubscriptionId();
        int sim2;
        try{
            sim2 = subscriptionInfos.get(1).getSubscriptionId();
        }
        catch (Exception e){
            sim2 = subscriptionInfos.get(0).getSubscriptionId();
        }

        if (sim_slot.equals("sim1")) {
            int position = sim1;
            runUssdCode(command, position, id);
        } else if (sim_slot.equals("sim2")) {
            int position = sim2;
            runUssdCode(command, position, id);
        }
    }


    private void updaterequestdetails(String response_message, String status, int requestID){
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apirequestdetail requestdetails = retrofit.create(apirequestdetail.class);
        requestdetailsbody body = new requestdetailsbody();

        body.setResponse_message(response_message);
        body.setStatus(status);
        body.setDevice(PreferenceUtils.getDeviceID(this));
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(this));
        Call<requestlistresponse> call = requestdetails.updateRequestDetails(headers, requestID, body);
        call.enqueue(new Callback<requestlistresponse>() {
            @Override
            public void onResponse(Call<requestlistresponse> call, Response<requestlistresponse> response) {
                updated = true;
                requestlistresponse resp = (requestlistresponse) response.body();
            }

            @Override
            public void onFailure(Call<requestlistresponse> call, Throwable t) {
            }
        });
    }



    @SuppressLint({"NewApi", "MissingPermission"})
    public void runUssdCode(String ussd, int position, int id) {
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg); //no need to change anything here
            }
        };
        @SuppressLint({"NewApi", "LocalSuppress"}) TelephonyManager.UssdResponseCallback ussdResponseCallback = new TelephonyManager.UssdResponseCallback() {
            @Override
            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                //if our request is successful then we get response here
                Log.d("BackgroundService", "onReceiveUssdResponse: "+response.toString());
                updaterequestdetails(response.toString(), "completed", id);
                PreferenceUtils.saveUpdateStatus("completed", getApplicationContext());
                PreferenceUtils.savePrevRequestId(String.valueOf(id), BackgroundService.this);
                PreferenceUtils.savePrevResponse(response.toString(), BackgroundService.this);
                sendNotification(response.toString(), "USSD");
            }

            @Override
            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {

                //request failures will be catched here
                updaterequestdetails("ussd dial successful", "completed", id);
                sendNotification("You have one incoming message from MsPlug.\n" +
                        " Please be sure to check back after some time for an update.\n" +
                        " Thank you", "USSD");
                PreferenceUtils.saveUpdateStatus("completed", getApplicationContext());
                PreferenceUtils.savePrevRequestId(String.valueOf(id), BackgroundService.this);
                PreferenceUtils.savePrevResponse("ussd dial successful", BackgroundService.this);
                /**
                updaterequestdetails("request failed "+request.toString(), "failed", id);
                Log.d(this.getClass().getName(), "response" + failureCode);
                PreferenceUtils.saveUpdateStatus("failed", getApplicationContext());
                Toast.makeText(BackgroundService.this, "request failed to dial", Toast.LENGTH_SHORT).show();
                **/
                //sendNotification(ussd, "failed to dial "+ussd, "USSD");
            }
        };
        manager.createForSubscriptionId(position).sendUssdRequest(ussd
                ,ussdResponseCallback,handler);
    }

    private void sendNotification(String response_message, String request_type) {
        NotificationCompat.Builder nb = helper.getChannelNotification(response_message, request_type);
        helper.getManager().notify(12345, nb.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}