package com.example.msplug.background_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.msplug.notification.App.CHANNEL_ID;
import static com.example.msplug.utils.Constants.serviceStatusConstants.ONLINE_STATUS;

public class BackgroundService extends Service {

    private static boolean sServiceHandleCacheEnabled = true;
    TelephonyManager telephonyManager;
    TelephonyManager.UssdResponseCallback ussdResponseCallback;
    Handler handler;
    boolean stillLoading;

    @Override
    public void onCreate() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
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

        //do heavy work in a background thread using intentService;

        Handler handlerx = new Handler();
        final int delay = 25000; // 1000 milliseconds == 1 second

        stillLoading = true;
        handlerx.postDelayed(new Runnable() {
            public void run() {
                refreshevery15sec();
                handlerx.postDelayed(this, delay);
            }
        }, delay);

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


    /**
    private ITelephony getITelephony() {
        // Keeps cache disabled until test fixes are checked into AOSP.
        if (!sServiceHandleCacheEnabled) {
            return ITelephony.Stub.asInterface(
                    TelephonyFrameworkInitializer
                            .getTelephonyServiceManager()
                            .getTelephonyServiceRegisterer()
                            .get());
        }

        if (sITelephony == null) {
            ITelephony temp = ITelephony.Stub.asInterface(
                    TelephonyFrameworkInitializer
                            .getTelephonyServiceManager()
                            .getTelephonyServiceRegisterer()
                            .get());
            synchronized (sCacheLock) {
                if (sITelephony == null && temp != null) {
                    try {
                        sITelephony = temp;
                        sITelephony.asBinder().linkToDeath(sServiceDeath, 0);
                    } catch (Exception e) {
                        // something has gone horribly wrong
                        sITelephony = null;
                    }
                }
            }
        }
        return sITelephony;
    }




    public static final String USSD_RESPONSE = "USSD_RESPONSE";
    public static final int USSD_RETURN_SUCCESS = 100;
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    public void sendUssdRequest(String ussdRequest,
                                final TelephonyManager.UssdResponseCallback callback, Handler handler) {
        //final TelephonyManager telephonyManager = this;

        ResultReceiver wrappedCallback = new ResultReceiver(handler) {
            @SuppressLint("NewApi")
            @Override
            protected void onReceiveResult(int resultCode, Bundle ussdResponse) {
                UssdResponse response = ussdResponse.getParcelable(USSD_RESPONSE);

                if (resultCode == USSD_RETURN_SUCCESS) {
                    callback.onReceiveUssdResponse(telephonyManager, response.getUssdRequest(),
                            response.getReturnMessage());
                } else {
                    callback.onReceiveUssdResponseFailed(telephonyManager,
                            response.getUssdRequest(), resultCode);
                }
            }
        };

        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.handleUssdRequest(getSubId(), ussdRequest, wrappedCallback);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#sendUSSDCode", e);
            UssdResponse response = new UssdResponse(ussdRequest, "");
            Bundle returnData = new Bundle();
            returnData.putParcelable(USSD_RESPONSE, response);
            wrappedCallback.send(USSD_ERROR_SERVICE_UNAVAIL, returnData);
        }
    }**/

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
                Toast.makeText(BackgroundService.this, "PATCH request sent with status: "+resp.getStatus(), Toast.LENGTH_SHORT).show();
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
            manager.sendUssdRequest("*123#", new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Log.d("BackgroundService", "onReceiveUssdResponse: "+response.toString());
                    updaterequestdetails(response.toString(), "completed", id);
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    updaterequestdetails("request failed "+request.toString(), "Failed", id);
                    Log.d(this.getClass().getName(), "response" + failureCode);
                }
            }, new Handler());
        }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
