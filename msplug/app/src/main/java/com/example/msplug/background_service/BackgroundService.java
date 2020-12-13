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
            v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(2000);
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
        Call<requestlistresponse> call = requestlist.getRequestList("EN1501Q5");
        call.enqueue(new Callback<requestlistresponse>() {
            @Override
            public void onResponse(Call<requestlistresponse> call, Response<requestlistresponse> response) {
                requestlistresponse resp = (requestlistresponse) response.body();
                String requesttype = resp.getRequest_type();
                String sim_slot = resp.getSim_slot();
                String command = resp.getCommand();
                int id = resp.getId();
                Toast.makeText(BackgroundService.this, "id for patch"+id+ " command: "+command, Toast.LENGTH_SHORT).show();
                Log.d("BackgroundService", "SCS"+ id);
                String device = resp.getDevice();
                String device_name = resp.getDevice_name();
                String recipient = resp.getReceipient();

                if (requesttype.equals("USSD")) {
                    Toast.makeText(BackgroundService.this, "dialing ussd", Toast.LENGTH_SHORT).show();
                    dialUSSD(sim_slot, command, id);
                    stillLoading = false;
                } else if (requesttype.equals("SMS")) {
                    Toast.makeText(BackgroundService.this, "sending sms", Toast.LENGTH_SHORT).show();
                    sendSMS(sim_slot, recipient, command, id);
                    stillLoading = false;
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
        updaterequestdetails(command + " sent to " + recipient + "successfull", "completed", id);
    }


    @SuppressLint("MissingPermission")
    private void dialUSSD(String sim_slot, String command, int id) {
        Toast.makeText(this, "dialer function called", Toast.LENGTH_SHORT).show();
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
        Call<requestlistresponse> call = requestdetails.updateRequestDetails(requestID, body);
        call.enqueue(new Callback<requestlistresponse>() {
            @Override
            public void onResponse(Call<requestlistresponse> call, Response<requestlistresponse> response) {
                requestlistresponse resp = (requestlistresponse) response.body();
                Toast.makeText(BackgroundService.this, "PATCH request sent with status: "+resp.getDevice_name(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<requestlistresponse> call, Throwable t) {
                Toast.makeText(BackgroundService.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void runUssdCode(String ussd, int position, int id) {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        manager.createForSubscriptionId(position);
        Log.d("networkprovider",manager.getNetworkOperatorName());
            manager.sendUssdRequest(ussd, new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Log.d("BackgroundService", "onReceiveUssdResponse: "+response.toString());
                    Toast.makeText(BackgroundService.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                    updaterequestdetails(response.toString(), "completed", id);
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    updaterequestdetails("request failed "+request.toString(), "failed", id);
                    Log.d(this.getClass().getName(), "response" + failureCode);
                    Toast.makeText(BackgroundService.this, "ussd code run failed "+request, Toast.LENGTH_SHORT).show();
                }
            }, new Handler());
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
